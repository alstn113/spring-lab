package io.github.alstn113.app.support

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisDataCleaner(
    private val redisTemplate: StringRedisTemplate
) {

    fun clear() {
        redisTemplate.connectionFactory!!
            .connection
            .serverCommands()
            .flushDb()
    }
}