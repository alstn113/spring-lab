package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.LogoutUseCase
import io.github.alstn113.rtr.application.port.out.RefreshTokenRegistry
import io.github.alstn113.rtr.application.port.out.TokenProvider
import org.springframework.stereotype.Service

@Service
class LogoutService(
    private val tokenProvider: TokenProvider,
    private val refreshTokenRegistry: RefreshTokenRegistry,
) : LogoutUseCase {

    override fun invoke(refreshToken: String?) {
        if (refreshToken == null) {
            return
        }

        val payload = tokenProvider.resolvePayloadFromRefreshToken(token = refreshToken)

        refreshTokenRegistry.revokeFamily(
            accountId = payload.accountId,
            familyId = payload.familyId
        )
    }
}