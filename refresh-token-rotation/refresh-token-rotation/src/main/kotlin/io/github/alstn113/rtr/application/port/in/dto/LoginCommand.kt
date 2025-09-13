package io.github.alstn113.rtr.application.port.`in`.dto

data class LoginCommand(
    val email: String,
    val password: String,
)
