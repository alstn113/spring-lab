package com.example.security.app.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtTokenProperties(
        String secretKey,
        long expirationTime
) {
}
