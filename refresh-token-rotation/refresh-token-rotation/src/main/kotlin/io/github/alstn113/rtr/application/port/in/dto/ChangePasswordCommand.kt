package io.github.alstn113.rtr.application.port.`in`.dto

data class ChangePasswordCommand(
    val accountId: Long,
    val currentPassword: String,
    val newPassword: String,
)
