package io.github.alstn113.rtr.application.port.`in`.dto

data class SignUpResult(
    val accountId: Long,
    val email: String,
    val name: String,
)