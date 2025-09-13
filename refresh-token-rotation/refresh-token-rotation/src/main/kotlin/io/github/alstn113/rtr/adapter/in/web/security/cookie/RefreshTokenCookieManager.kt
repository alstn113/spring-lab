package io.github.alstn113.rtr.adapter.`in`.web.security.cookie

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class RefreshTokenCookieManager(
    private val properties: RefreshTokenCookieProperties,
) {

    fun createCookie(token: String): ResponseCookie {
        return ResponseCookie.from(properties.name, token)
            .maxAge(properties.expiration)
            .domain(properties.domain)
            .path(properties.path)
            .httpOnly(properties.httpOnly)
            .secure(properties.secure)
            .sameSite(properties.sameSite)
            .build()
    }

    fun clearCookie(): ResponseCookie {
        return ResponseCookie.from(properties.name, "")
            .maxAge(0)
            .domain(properties.domain)
            .path(properties.path)
            .httpOnly(properties.httpOnly)
            .secure(properties.secure)
            .sameSite(properties.sameSite)
            .build()
    }

    fun extractToken(request: HttpServletRequest): String? {
        return request.cookies
            ?.firstOrNull { it.name == properties.name }
            ?.value
    }
}