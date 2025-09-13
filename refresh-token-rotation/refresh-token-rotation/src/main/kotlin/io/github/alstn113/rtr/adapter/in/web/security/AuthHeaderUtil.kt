package io.github.alstn113.rtr.adapter.`in`.web.security

import jakarta.servlet.http.HttpServletRequest

object AuthHeaderUtil {

    private const val AUTHORIZATION_HEADER = "Authorization"
    private const val BEARER_PREFIX = "Bearer "

    fun extractBearerToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)
        if (authHeader.isNullOrBlank()) return null
        if (!authHeader.startsWith(BEARER_PREFIX)) return null

        val token = authHeader.removePrefix(BEARER_PREFIX).trim()
        return token.ifBlank { null }
    }
}