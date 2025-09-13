package io.github.alstn113.rtr.application.port.`in`

fun interface LogoutAllUseCase {

    operator fun invoke(accountId: Long)
}