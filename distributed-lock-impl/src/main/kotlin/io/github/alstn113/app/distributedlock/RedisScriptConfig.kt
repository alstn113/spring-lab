package io.github.alstn113.app.distributedlock

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.scripting.support.ResourceScriptSource

@Configuration
class RedisScriptConfig {

    @Bean("tryAcquireScript")
    fun tryAcquireScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/try-acquire.lua")))
            setResultType(Long::class.java)
        }
    }

    @Bean("unlockScript")
    fun unlockScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/unlock.lua")))
            setResultType(Long::class.java)
        }
    }

    @Bean("unlockPublishScript")
    fun unlockPublishScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/unlock-publish.lua")))
            setResultType(Long::class.java)
        }
    }
}