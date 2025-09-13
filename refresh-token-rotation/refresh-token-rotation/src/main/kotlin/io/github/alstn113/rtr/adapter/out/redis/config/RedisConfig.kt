package io.github.alstn113.rtr.adapter.out.redis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfig(
    private val properties: RedisProperties,
) {

    @Bean
    fun stringRedisTemplate(): StringRedisTemplate {
        return StringRedisTemplate().apply {
            connectionFactory = lettuceConnectionFactory()
        }
    }

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val configuration = RedisStandaloneConfiguration().apply {
            hostName = properties.host
            port = properties.port
        }
        val clientConfigBuilder = LettuceClientConfiguration.builder()
        return LettuceConnectionFactory(configuration, clientConfigBuilder.build())
    }
}