package io.github.alstn113.rtr.application.port.out

import io.github.alstn113.rtr.application.port.out.dto.AccessTokenPayload
import io.github.alstn113.rtr.application.port.out.dto.RefreshTokenPayload

interface TokenProvider {

    fun generateAccessToken(payload: AccessTokenPayload): String

    fun generateRefreshToken(payload: RefreshTokenPayload): String

    fun resolvePayloadFromAccessToken(token: String): AccessTokenPayload

    fun resolvePayloadFromRefreshToken(token: String): RefreshTokenPayload
}