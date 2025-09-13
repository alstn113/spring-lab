package io.github.alstn113.rtr.domain.account.exception

import io.github.alstn113.rtr.domain.BaseException

class InvalidNameLengthException(
    minLength: Int,
    maxLength: Int,
    cause: Throwable? = null,
) : BaseException("이름은 $minLength ~ $maxLength 글자여야 합니다.", cause)