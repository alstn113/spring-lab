package io.github.alstn113.app.distributedlock.spinlock

import io.github.alstn113.app.distributedlock.DistributedLock
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class SpinLock(
    private val redisTemplate: StringRedisTemplate,
    @Qualifier("tryAcquireScript") private val tryAcquireScript: RedisScript<Long>,
    @Qualifier("unlockScript") private val unlockScript: RedisScript<Long>,
) : DistributedLock {

    override fun tryLock(key: String, waitTime: Duration, leaseTime: Duration): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val waitTimeMillis = waitTime.toMillis()
        val leaseTimeMillis = leaseTime.toMillis()

        var backOffMillis = 1L

        while (System.currentTimeMillis() - currentTimeMillis < waitTimeMillis) {
            val result = redisTemplate.execute(
                tryAcquireScript,
                listOf(key),
                leaseTimeMillis.toString(),
                getLockOwnerId(),
            )

            if (result == -1L) {
                return true
            }

            try {
                Thread.sleep(backOffMillis)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                return false
            }

            backOffMillis = minOf(backOffMillis * 2, 128L)
        }

        return false
    }

    override fun unlock(key: String) {
        redisTemplate.execute(
            unlockScript,
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