package io.github.alstn113.rtr.adapter.`in`.web.controller.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)