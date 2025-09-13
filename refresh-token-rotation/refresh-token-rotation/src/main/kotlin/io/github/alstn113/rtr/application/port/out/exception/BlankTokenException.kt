package io.github.alstn113.rtr.application.port.out.exception

class BlankTokenException(
    cause: Throwable? = null,
) : TokenException("토큰이 비어있습니다.", cause)