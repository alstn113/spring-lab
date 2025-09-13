package io.github.alstn113.rtr.application.port.`in`

import io.github.alstn113.rtr.application.port.`in`.dto.LoginCommand
import io.github.alstn113.rtr.application.port.`in`.dto.LoginResult

fun interface LoginUseCase {

    operator fun invoke(command: LoginCommand): LoginResult
}