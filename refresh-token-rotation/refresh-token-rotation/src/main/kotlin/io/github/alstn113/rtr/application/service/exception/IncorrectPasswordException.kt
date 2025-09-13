package io.github.alstn113.rtr.application.service.exception

import io.github.alstn113.rtr.domain.BaseException

class IncorrectPasswordException(
    cause: Throwable? = null,
) : BaseException("비밀번호가 일치하지 않습니다.", cause)