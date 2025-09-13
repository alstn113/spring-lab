package io.github.alstn113.rtr.adapter.out.security.jwt

import io.github.alstn113.rtr.application.port.out.TokenProvider
import io.github.alstn113.rtr.application.port.out.dto.AccessTokenPayload
import io.github.alstn113.rtr.application.port.out.dto.RefreshTokenPayload
import io.github.alstn113.rtr.application.port.out.exception.BlankTokenException
import io.github.alstn113.rtr.application.port.out.exception.InvalidTokenException
import io.github.alstn113.rtr.application.port.out.exception.TokenExpiredException
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val properties: JwtTokenProperties,
) : TokenProvider {

    private val accessTokenSecretKey = Keys.hmacShaKeyFor(properties.accessToken.secretKey.toByteArray())
    private val refreshTokenSecretKey = Keys.hmacShaKeyFor(properties.refreshToken.secretKey.toByteArray())
    private val accessTokenExpiration = properties.accessToken.expiration
    private val refreshTokenExpiration = properties.refreshToken.expiration

    override fun generateAccessToken(payload: AccessTokenPayload): String {
        return tokenBuilder(TokenType.ACCESS)
            .subject(payload.accountId.toString())
            .compact()
    }

    override fun generateRefreshToken(payload: RefreshTokenPayload): String {
        return tokenBuilder(TokenType.REFRESH)
            .id(payload.jti)
            .subject(payload.accountId.toString())
            .claim(FAMILY_ID_CLAIM, payload.familyId)
            .compact()
    }

    override fun resolvePayloadFromAccessToken(token: String): AccessTokenPayload {
        val claims = parseClaims(TokenType.ACCESS, token)
        val accountId = claims.subject.toLong()

        return AccessTokenPayload(accountId = accountId)
    }

    override fun resolvePayloadFromRefreshToken(token: String): RefreshTokenPayload {
        val claims = parseClaims(TokenType.REFRESH, token)
        val accountId = claims.subject.toLong()
        val jti = claims.id
        val familyId = claims[FAMILY_ID_CLAIM] as? String
            ?: throw InvalidTokenException()

        return RefreshTokenPayload(
            accountId = accountId,
            familyId = familyId,
            jti = jti,
        )
    }

    private fun tokenBuilder(type: TokenType): JwtBuilder {
        val now = Date()
        val expiration = Date(now.time + getExpirationTime(type).toMillis())

        return Jwts.builder()
            .issuedAt(now)
            .expiration(expiration)
            .signWith(getSecretKey(type))
    }

    private fun parseClaims(type: TokenType, token: String): Claims {
        if (token.isBlank()) {
            throw BlankTokenException()
        }

        try {
            val claimsJws: Jws<Claims> = Jwts.parser()
                .verifyWith(getSecretKey(type))
                .build()
                .parseSignedClaims(token)

            return claimsJws.payload
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException(e)
        } catch (e: JwtException) {
            throw InvalidTokenException(e)
        }
    }

    private fun getSecretKey(type: TokenType): SecretKey {
        return when (type) {
            TokenType.ACCESS -> accessTokenSecretKey
            TokenType.REFRESH -> refreshTokenSecretKey
        }
    }

    private fun getExpirationTime(type: TokenType): Duration {
        return when (type) {
            TokenType.ACCESS -> accessTokenExpiration
            TokenType.REFRESH -> refreshTokenExpiration
        }
    }

    companion object {
        private const val FAMILY_ID_CLAIM = "familyId"
    }
}