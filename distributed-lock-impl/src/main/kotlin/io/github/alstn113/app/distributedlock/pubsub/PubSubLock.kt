package io.github.alstn113.app.distributedlock.pubsub

import io.github.alstn113.app.distributedlock.DistributedLock
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


@Component
class PubSubLock(
    private val redisTemplate: StringRedisTemplate,
    private val manager: LockNotificationManager,
    @Qualifier("tryAcquireScript") private val tryAcquireScript: RedisScript<Long>,
    @Qualifier("unlockPublishScript") private val unlockPublishScript: RedisScript<Long>,
) : DistributedLock {

    override fun tryLock(key: String, waitTime: Duration, leaseTime: Duration): Boolean {
        val deadlineMillis = System.currentTimeMillis() + waitTime.toMillis()

        val ttl = tryAcquire(key, leaseTime)
        if (ttl == -1L) {
            return true
        }

        if (isDeadlineExceeded(deadlineMillis)) {
            return false
        }

        val semaphore = manager.subscribe(key)

        try {
            return waitForUnlock(
                key = key,
                leaseTime = leaseTime,
                deadlineMillis = deadlineMillis,
                semaphore = semaphore
            )
        } finally {
            manager.unsubscribe(key)
        }
    }

    private fun waitForUnlock(
        key: String,
        leaseTime: Duration,
        deadlineMillis: Long,
        semaphore: Semaphore,
    ): Boolean {
        while (true) {
            if (isDeadlineExceeded(deadlineMillis)) {
                return false
            }

            // 대기하기 전에 알림이 올 수 있으므로 즉시 재시도
            val ttl = tryAcquire(key, leaseTime)
            if (ttl == -1L) {
                return true
            }

            if (isDeadlineExceeded(deadlineMillis)) {
                return false
            }

            // 락이 해제되기를 대기, 알림 시 여러 스레드 중 하나만 깨어날 수 있음
            val remainTimeMillis = deadlineMillis - System.currentTimeMillis()
            val acquired = semaphore.tryAcquire(remainTimeMillis, TimeUnit.MILLISECONDS)
            if (!acquired) { // 타임아웃 발생
                return false
            }
        }
    }

    override fun unlock(key: String) {
        val notifyChannel = "${key}${LockNotificationManager.NOTIFY_SUFFIX}"

        redisTemplate.execute(
            unlockPublishScript,
            listOf(key),
            getLockOwnerId(),
            notifyChannel,
        )
    }

    /**
     * @return 락을 획득하거나 재진입한 경우 -1 반환, 실패 시 남은 TTL(밀리초) 반환
     */
    private fun tryAcquire(key: String, leaseTime: Duration): Long {
        val leaseTimeMillis = leaseTime.toMillis()

        return redisTemplate.execute(
            tryAcquireScript,
            listOf(key),
            leaseTimeMillis.toString(),
            getLockOwnerId()
        )
    }

    private fun isDeadlineExceeded(deadlineMillis: Long): Boolean {
        return System.currentTimeMillis() > deadlineMillis
    }

    private fun getLockOwnerId(): String {
        val threadId = Thread.currentThread().threadId()
        return "$INSTANCE_ID:$threadId"
    }

    companion object {
        private val INSTANCE_ID = UUID.randomUUID().toString()
    }
}