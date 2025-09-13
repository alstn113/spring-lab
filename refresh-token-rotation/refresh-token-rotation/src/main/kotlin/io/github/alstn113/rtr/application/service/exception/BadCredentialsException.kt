package io.github.alstn113.rtr.application.service.exception

import io.github.alstn113.rtr.domain.BaseException

class BadCredentialsException(
    cause: Throwable? = null,
) : BaseException("잘못된 비밀번호 입니다.", cause)