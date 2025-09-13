package io.github.alstn113.rtr.application.port.`in`

import io.github.alstn113.rtr.application.port.`in`.dto.SignUpCommand
import io.github.alstn113.rtr.application.port.`in`.dto.SignUpResult

fun interface SignUpUseCase {

    operator fun invoke(command: SignUpCommand): SignUpResult
}