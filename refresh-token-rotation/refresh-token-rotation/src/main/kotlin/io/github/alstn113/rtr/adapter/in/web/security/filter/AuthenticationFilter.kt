package io.github.alstn113.rtr.adapter.`in`.web.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.alstn113.rtr.adapter.`in`.web.common.ApiResponse
import io.github.alstn113.rtr.adapter.`in`.web.security.AccountPrincipal
import io.github.alstn113.rtr.adapter.`in`.web.security.AttributeNames
import io.github.alstn113.rtr.adapter.`in`.web.security.AuthHeaderUtil
import io.github.alstn113.rtr.application.port.out.TokenProvider
import io.github.alstn113.rtr.application.port.out.exception.TokenException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

class AuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val objectMapper: ObjectMapper,
    private val allowListPatterns: List<String>,
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return allowListPatterns.any { pattern -> pathMatcher.match(pattern, request.requestURI) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val principal = resolvePrincipal(request)
            request.setAttribute(AttributeNames.AUTHENTICATION_PRINCIPAL, principal)
            filterChain.doFilter(request, response)
        } catch (e: TokenException) {
            log.warn("[TokenException] 유효하지 않은 토큰입니다. {}", e.message, e)
            writeErrorResponse(response, message = e.message ?: "유효하지 않은 토큰입니다.")
        }
    }

    private fun resolvePrincipal(request: HttpServletRequest): AccountPrincipal {
        val accessToken = AuthHeaderUtil.extractBearerToken(request)
        return if (accessToken.isNullOrBlank()) {
            AccountPrincipal.ANONYMOUS
        } else {
            val payload = tokenProvider.resolvePayloadFromAccessToken(accessToken)
            AccountPrincipal(accountId = payload.accountId)
        }
    }

    private fun writeErrorResponse(response: HttpServletResponse, message: String) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorResponse = ApiResponse.error(errorMessage = message)
        val body = objectMapper.writeValueAsString(errorResponse)
        response.writer.write(body)
        response.flushBuffer()
    }
}