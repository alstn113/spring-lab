package io.github.alstn113.rtr.adapter.`in`.web.controller.dto

import io.github.alstn113.rtr.application.port.`in`.dto.ChangePasswordCommand

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
) {

    fun toCommand(accountId: Long): ChangePasswordCommand {
        return ChangePasswordCommand(
            accountId = accountId,
            currentPassword = currentPassword,
            newPassword = newPassword,
        )
    }
}
