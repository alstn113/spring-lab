package io.github.alstn113.rtr.adapter.`in`.web.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("web.cors")
class CorsProperties(
    val allowedOrigins: List<String>,
    val allowedMethods: List<String>,
    val allowedHeaders: List<String>,
    val allowCredentials: Boolean,
    val maxAge: Long,
)