package io.github.alstn113.rtr.application.port.out.exception

class TokenExpiredException(
    cause: Throwable? = null,
) : TokenException("토큰이 만료되었습니다.", cause)