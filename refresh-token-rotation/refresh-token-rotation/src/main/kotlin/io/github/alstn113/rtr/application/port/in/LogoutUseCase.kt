package io.github.alstn113.rtr.application.port.`in`

fun interface LogoutUseCase {

    operator fun invoke(refreshToken: String?)
}