package io.github.alstn113.app.distributedlock.pubsub

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

@Component
class LockNotificationSubscriber : MessageListener {

    private val log = LoggerFactory.getLogger(javaClass)
    private val stringSerializer = StringRedisSerializer.UTF_8
    private val registry = ConcurrentHashMap<String, LockWaitEntry>()

    private data class LockWaitEntry(
        val semaphore: Semaphore,
        val refCount: AtomicInteger = AtomicInteger(1),
    )


    fun acquire(key: String): Semaphore {
        val entry = registry.compute(key) { _, existing ->
            if (existing == null) {
                log.info("새로운 LockWaitEntry 생성: key={}", key)
                LockWaitEntry(Semaphore(0))
            } else {
                existing.refCount.incrementAndGet()
                log.info("기존 LockWaitEntry 재사용: key={}, refCount={}", key, existing.refCount.get())
                existing
            }
        }!!
        return entry.semaphore
    }

    fun release(key: String) {
        registry.computeIfPresent(key) { _, entry ->
            val remain = entry.refCount.decrementAndGet()
            log.info("LockWaitEntry 해제: key={}, 남은 refCount={}", key, remain)
            if (remain <= 0) {
                log.info("LockWaitEntry 제거: key={}", key)
                null
            } else {
                entry
            }
        }
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val channel = stringSerializer.deserialize(message.channel) ?: return
        if (!channel.endsWith(NOTIFY_SUFFIX)) {
            return
        }
        val key = channel.removeSuffix(NOTIFY_SUFFIX)
        if (key.isBlank()) {
            return
        }

        val entry = registry[key]
        entry?.semaphore?.release()
        log.info("락 해제 알림 수신: key={}, 남은 대기자={}", key, entry?.semaphore?.availablePermits() ?: 0)
    }

    companion object {
        const val NOTIFY_SUFFIX = ":notify"
        const val NOTIFY_PATTERN = "*$NOTIFY_SUFFIX"
    }
}