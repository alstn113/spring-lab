package io.github.alstn113.rtr.adapter.out.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("auth")
data class JwtTokenProperties(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
) {

    data class AccessToken(
        val secretKey: String,
        val expiration: Duration,
    )

    data class RefreshToken(
        val secretKey: String,
        val expiration: Duration,
    )
}
