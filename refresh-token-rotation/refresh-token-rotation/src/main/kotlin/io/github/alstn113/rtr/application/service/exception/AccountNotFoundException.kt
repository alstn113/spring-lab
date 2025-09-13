package io.github.alstn113.rtr.application.service.exception

import io.github.alstn113.rtr.domain.BaseException

class AccountNotFoundException(
    cause: Throwable? = null,
) : BaseException("계정을 찾을 수 없습니다.", cause)