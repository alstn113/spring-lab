package io.github.alstn113.rtr.application.port.out.dto

data class RefreshTokenPayload(
    val accountId: Long,
    val familyId: String,
    val jti: String,
)