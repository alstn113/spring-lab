package io.github.alstn113.rtr.adapter.`in`.web.controller.exception

import io.github.alstn113.rtr.domain.BaseException

class RefreshTokenCookieMissingException(
    cause: Throwable? = null,
) : BaseException("리프레시 토큰 쿠키가 존재하지 않습니다.", cause)