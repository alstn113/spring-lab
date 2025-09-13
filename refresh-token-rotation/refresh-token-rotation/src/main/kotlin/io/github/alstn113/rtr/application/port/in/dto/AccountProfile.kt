package io.github.alstn113.rtr.application.port.`in`.dto

import java.time.LocalDateTime

data class AccountProfile(
    val accountId: Long,
    val email: String,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
