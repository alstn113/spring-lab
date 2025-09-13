package io.github.alstn113.rtr.application.port.`in`.dto

data class SignUpCommand(
    val email: String,
    val password: String,
    val name: String,
)
