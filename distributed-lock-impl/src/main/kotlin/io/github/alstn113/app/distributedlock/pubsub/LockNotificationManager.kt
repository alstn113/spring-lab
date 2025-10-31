package io.github.alstn113.app.distributedlock.pubsub

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

@Component
class LockNotificationManager(
    private val container: RedisMessageListenerContainer,
) : MessageListener {

    private val log = LoggerFactory.getLogger(javaClass)
    private val serializer = StringRedisSerializer.UTF_8
    private val registry = ConcurrentHashMap<String, LockWaitEntry>()

    private data class LockWaitEntry(
        val semaphore: Semaphore = Semaphore(0),
        val counter: AtomicInteger = AtomicInteger(1),
    )

    fun subscribe(key: String): Semaphore {
        var created = false
        val entry = registry.compute(key) { _, existing ->
            if (existing == null) {
                log.info("새로운 LockWaitEntry 생성 및 구독: key={}", key)
                created = true
                LockWaitEntry()
            } else {
                existing.counter.incrementAndGet()
                log.info("기존 LockWaitEntry 재사용: key={}, refCount={}", key, existing.counter.get())
                existing
            }
        }!!

        if (created) {
            container.addMessageListener(this, ChannelTopic(key + NOTIFY_SUFFIX))
        }

        return entry.semaphore
    }

    fun unsubscribe(key: String) {
        registry.computeIfPresent(key) { _, entry ->
            val remain = entry.counter.decrementAndGet()
            log.info("LockWaitEntry 해제: key={}, 남은 refCount={}", key, remain)

            if (remain <= 0) {
                log.info("LockWaitEntry 제거 및 구독 해제: key={}", key)
                container.removeMessageListener(this, ChannelTopic(key + NOTIFY_SUFFIX))
                null
            } else {
                entry
            }
        }
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val channel = serializer.deserialize(message.channel) ?: return
        if (!channel.endsWith(NOTIFY_SUFFIX)) {
            return
        }

        val key = channel.removeSuffix(NOTIFY_SUFFIX)
        if (key.isBlank()) {
            return
        }

        val entry = registry[key] ?: return
        entry.semaphore.release()
        log.info("락 해제 알림 수신: key={}, 남은 대기자={}", key, entry?.counter ?: 0)
    }

    companion object {
        const val NOTIFY_SUFFIX = ":notify"
    }
}