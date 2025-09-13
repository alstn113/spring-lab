package io.github.alstn113.rtr.application.port.out.exception

class InvalidTokenException(
    throwable: Throwable? = null,
) : TokenException("유효하지 않은 토큰입니다.", throwable)