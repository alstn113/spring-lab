package io.github.alstn113.rtr.application.port.out.exception

import io.github.alstn113.rtr.domain.BaseException

open class TokenException(
    message: String,
    cause: Throwable? = null,
) : BaseException(message, cause)