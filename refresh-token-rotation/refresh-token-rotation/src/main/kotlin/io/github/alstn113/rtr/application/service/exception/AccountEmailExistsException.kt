package io.github.alstn113.rtr.application.service.exception

import io.github.alstn113.rtr.domain.BaseException

class AccountEmailExistsException(
    cause: Throwable? = null,
) : BaseException("이미 존재하는 이메일입니다.", cause)