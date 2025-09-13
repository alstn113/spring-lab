package io.github.alstn113.rtr.adapter.out.redis.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.redis")
data class RedisProperties(
    val host: String,
    val port: Int,
)
