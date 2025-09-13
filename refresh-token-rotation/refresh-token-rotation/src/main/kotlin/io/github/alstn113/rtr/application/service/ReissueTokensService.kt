package io.github.alstn113.rtr.application.service

import io.github.alstn113.rtr.application.port.`in`.ReissueTokensUseCase
import io.github.alstn113.rtr.application.port.`in`.dto.ReissueTokensResult
import io.github.alstn113.rtr.application.port.out.RefreshTokenRegistry
import io.github.alstn113.rtr.application.port.out.TokenProvider
import io.github.alstn113.rtr.application.port.out.dto.AccessTokenPayload
import io.github.alstn113.rtr.application.port.out.dto.RefreshTokenPayload
import io.github.alstn113.rtr.application.service.exception.InvalidRefreshTokenException
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReissueTokensService(
    private val tokenProvider: TokenProvider,
    private val refreshTokenRegistry: RefreshTokenRegistry,
) : ReissueTokensUseCase {

    override fun invoke(refreshToken: String): ReissueTokensResult {
        val payload = tokenProvider.resolvePayloadFromRefreshToken(token = refreshToken)
        val newJti = UUID.randomUUID().toString()

        val success = refreshTokenRegistry.rotate(
            accountId = payload.accountId,
            familyId = payload.familyId,
            oldJti = payload.jti,
            newJti = newJti
        )
        if (!success) {
            throw InvalidRefreshTokenException()
        }

        val accessToken = tokenProvider.generateAccessToken(
            AccessTokenPayload(accountId = payload.accountId)
        )
        val newRefreshToken = tokenProvider.generateRefreshToken(
            RefreshTokenPayload(
                accountId = payload.accountId,
                familyId = payload.familyId,
                jti = newJti,
            )
        )

        return ReissueTokensResult(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
        )
    }
}