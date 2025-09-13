package io.github.alstn113.rtr.adapter.`in`.web.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.alstn113.rtr.adapter.`in`.web.common.ApiResponse
import io.github.alstn113.rtr.adapter.`in`.web.security.AccountPrincipal
import io.github.alstn113.rtr.adapter.`in`.web.security.AttributeNames
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

class AuthorizationFilter(
    private val objectMapper: ObjectMapper,
    private val allowListPatterns: List<String>,
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (request.method == HttpMethod.OPTIONS.name()) {
            return true
        }
        return allowListPatterns.any { pattern -> pathMatcher.match(pattern, request.requestURI) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val principal = request.getAttribute(AttributeNames.AUTHENTICATION_PRINCIPAL) as AccountPrincipal?
        if (principal == null || principal.isAnonymous()) {
            writeErrorResponse(response)
            return
        }

        // 권한 검사(Forbidden 403)가 필요한 경우 추가 가능

        filterChain.doFilter(request, response)
    }

    private fun writeErrorResponse(response: HttpServletResponse) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorResponse = ApiResponse.error(errorMessage = "인증이 필요합니다.")
        val body = objectMapper.writeValueAsString(errorResponse)
        response.writer.write(body)
    }
}