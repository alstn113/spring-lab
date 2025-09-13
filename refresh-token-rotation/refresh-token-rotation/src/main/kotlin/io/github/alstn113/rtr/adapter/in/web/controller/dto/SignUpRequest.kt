package io.github.alstn113.rtr.adapter.`in`.web.controller.dto

import io.github.alstn113.rtr.application.port.`in`.dto.SignUpCommand

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
) {

    fun toCommand(): SignUpCommand {
        return SignUpCommand(
            email = email,
            password = password,
            name = name
        )
    }
}
