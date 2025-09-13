package io.github.alstn113.rtr.adapter.`in`.web.controller.dto

import io.github.alstn113.rtr.application.port.`in`.dto.LoginCommand

data class LoginRequest(
    val email: String,
    val password: String,
) {

    fun toCommand(): LoginCommand {
        return LoginCommand(
            email = email,
            password = password
        )
    }
}
