package io.github.alstn113.rtr.application.port.out.dto

import java.time.LocalDateTime

data class AccountSummary(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
