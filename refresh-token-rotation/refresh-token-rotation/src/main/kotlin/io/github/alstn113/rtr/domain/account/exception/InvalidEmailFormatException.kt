package io.github.alstn113.rtr.domain.account.exception

import io.github.alstn113.rtr.domain.BaseException

class InvalidEmailFormatException(
    cause: Throwable? = null,
) : BaseException("이메일 형식이 올바르지 않습니다.", cause)