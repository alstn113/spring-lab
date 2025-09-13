package io.github.alstn113.rtr.adapter.out.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("auth.refresh-token")
data class RefreshTokenProperties(
    val expiration: Duration,
    val overlapPeriod: Duration,
)
