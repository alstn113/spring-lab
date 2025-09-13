package io.github.alstn113.rtr.application.port.`in`

import io.github.alstn113.rtr.application.port.`in`.dto.ReissueTokensResult

fun interface ReissueTokensUseCase {

    operator fun invoke(refreshToken: String): ReissueTokensResult
}