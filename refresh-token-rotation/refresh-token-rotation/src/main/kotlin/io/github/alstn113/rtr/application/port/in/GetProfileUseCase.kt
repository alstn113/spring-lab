package io.github.alstn113.rtr.application.port.`in`

import io.github.alstn113.rtr.application.port.`in`.dto.AccountProfile

fun interface GetProfileUseCase {

    operator fun invoke(accountId: Long): AccountProfile
}