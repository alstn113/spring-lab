package io.github.alstn113.rtr.adapter.out.redis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.scripting.support.ResourceScriptSource

@Configuration
class RedisScriptConfig {

    @Bean("registerRefreshTokenScript")
    fun registerRefreshTokenScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/register-refresh-token.lua")))
            setResultType(Long::class.java)
        }
    }

    @Bean("rotateRefreshTokenScript")
    fun rotateRefreshTokenScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/rotate-refresh-token.lua")))
            setResultType(Long::class.java)
        }
    }

    @Bean("revokeRefreshTokenFamilyScript")
    fun revokeRefreshTokenFamilyScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/revoke-refresh-token-family.lua")))
            setResultType(Long::class.java)
        }
    }

    @Bean("revokeRefreshTokenFamiliesScript")
    fun revokeRefreshTokenFamiliesScript(): RedisScript<Long> {
        return DefaultRedisScript<Long>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("scripts/revoke-refresh-token-families.lua")))
            setResultType(Long::class.java)
        }
    }
}