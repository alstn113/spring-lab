package io.github.alstn113.rtr.application.port.`in`

import io.github.alstn113.rtr.application.port.`in`.dto.ChangePasswordCommand

fun interface ChangePasswordUseCase {

    operator fun invoke(command: ChangePasswordCommand)
}