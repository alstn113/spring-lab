package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.LogoutAllUseCase
import io.github.alstn113.rtr.application.port.out.RefreshTokenRegistry
import org.springframework.stereotype.Service

@Service
class LogoutAllUseCase(
    private val refreshTokenRegistry: RefreshTokenRegistry,
) : LogoutAllUseCase {

    override fun invoke(accountId: Long) {
        refreshTokenRegistry.revokeAll(accountId = accountId)
    }
}