package io.github.alstn113.rtr.domain

abstract class BaseException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)