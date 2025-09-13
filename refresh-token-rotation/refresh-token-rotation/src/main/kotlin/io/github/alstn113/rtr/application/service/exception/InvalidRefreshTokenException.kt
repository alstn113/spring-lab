package io.github.alstn113.rtr.application.service.exception

import io.github.alstn113.rtr.domain.BaseException

class InvalidRefreshTokenException(
    cause: Throwable? = null,
) : BaseException("유효하지 않은 리프레시 토큰입니다.", cause)