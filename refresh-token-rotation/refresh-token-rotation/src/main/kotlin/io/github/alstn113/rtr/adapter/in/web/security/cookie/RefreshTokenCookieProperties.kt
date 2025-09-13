package io.github.alstn113.rtr.adapter.`in`.web.security.cookie

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("auth.refresh-token-cookie")
data class RefreshTokenCookieProperties(
    val name: String,
    val domain: String,
    val path: String,
    val expiration: Duration,
    val httpOnly: Boolean,
    val secure: Boolean,
    val sameSite: String,
)