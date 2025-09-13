package io.github.alstn113.rtr.application.port.`in`.dto

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
)
