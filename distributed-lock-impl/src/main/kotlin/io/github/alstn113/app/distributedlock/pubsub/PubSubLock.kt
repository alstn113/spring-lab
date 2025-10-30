package io.github.alstn113.app.distributedlock.pubsub

import io.github.alstn113.app.distributedlock.DistributedLock
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


@Component
class PubSubLock(
    private val redisTemplate: StringRedisTemplate,
    private val subscriber: LockNotificationSubscriber,
    @Qualifier("tryAcquireScript") private val tryAcquireScript: RedisScript<Long>,
    @Qualifier("unlockPublishScript") private val unlockPublishScript: RedisScript<Long>,
) : DistributedLock {

    override fun tryLock(key: String, waitTime: Duration, leaseTime: Duration): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val waitTimeMillis = waitTime.toMillis()
        val leaseTimeMillis = leaseTime.toMillis()

        var ttl = redisTemplate.execute(
            tryAcquireScript,
            listOf(key),
            leaseTimeMillis.toString(),
            getLockOwnerId()
        )

        if (ttl == -1L) {
            return true
        }

        var remainingTimeMillis = waitTimeMillis - (System.currentTimeMillis() - currentTimeMillis)
        if (remainingTimeMillis <= 0) {
            return false
        }

        val semaphore = subscriber.acquire(key)

        try {
            // 락 해제 알림이 오고, 알림을 기다릴 수 있으므로 한 번 더 시도
            ttl = redisTemplate.execute(
                tryAcquireScript,
                listOf(key),
                leaseTimeMillis.toString(),
                getLockOwnerId()
            )

            if (ttl == -1L) {
                return true
            }

            while (true) {
                remainingTimeMillis = waitTimeMillis - (System.currentTimeMillis() - currentTimeMillis)
                if (remainingTimeMillis <= 0) {
                    return false
                }

                // onMessage 에서 release 호출 시 semaphore 가 증가하면 스레드 중 하나만 획득에 성공
                val notified = semaphore.tryAcquire(remainingTimeMillis, TimeUnit.MILLISECONDS)
                if (!notified) {
                    return false
                }

                val retry = redisTemplate.execute(
                    tryAcquireScript,
                    listOf(key),
                    leaseTimeMillis.toString(),
                    getLockOwnerId()
                )

                if (retry == -1L) {
                    return true
                }

                // 성공 시에만 release 를 호출하므로 실패한 경우는 다음 알림을 기다린다.
            }
        } catch (ie: InterruptedException) {
            Thread.currentThread().interrupt()
            return false
        } finally {
            subscriber.release(key)
        }
    }

    override fun unlock(key: String) {
        redisTemplate.execute(
            unlockPublishScript,
            listOf(key),
            getLockOwnerId(),
        )
    }

    private fun getLockOwnerId(): String {
        val threadId = Thread.currentThread().threadId()
        return "$INSTANCE_ID:$threadId"
    }

    companion object {
        private val INSTANCE_ID = UUID.randomUUID().toString()
    }
}