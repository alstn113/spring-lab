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

        while (System.currentTimeMillis() - currentTimeMillis < waitTimeMillis) {
            val result = redisTemplate.execute(
                tryAcquireScript,
                listOf(key),
                leaseTimeMillis.toString(),
                getLockOwnerId()
            )

            if (result == -1L) {
                return true
            }

            val semaphore = subscriber.acquire(key)

            val remainingTimeMillis = waitTimeMillis - (System.currentTimeMillis() - currentTimeMillis)
            if (remainingTimeMillis <= 0) {
                return false
            }

            try {
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
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
                return false
            } finally {
                subscriber.release(key)
            }
        }

        return false
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