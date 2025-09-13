package io.github.alstn113.rtr.adapter.`in`.web.common

import io.github.alstn113.rtr.adapter.`in`.web.controller.exception.RefreshTokenCookieMissingException
import io.github.alstn113.rtr.adapter.`in`.web.security.cookie.RefreshTokenCookieManager
import io.github.alstn113.rtr.application.port.out.exception.BlankTokenException
import io.github.alstn113.rtr.application.port.out.exception.InvalidTokenException
import io.github.alstn113.rtr.application.port.out.exception.TokenExpiredException
import io.github.alstn113.rtr.application.service.exception.*
import io.github.alstn113.rtr.domain.BaseException
import io.github.alstn113.rtr.domain.account.exception.InvalidEmailFormatException
import io.github.alstn113.rtr.domain.account.exception.InvalidNameLengthException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val cookieManager: RefreshTokenCookieManager,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        ex: BaseException,
    ): ResponseEntity<ApiResponse<Unit>> {
        log.warn("[BaseException]: {}", ex.message, ex)

        val response = ApiResponse.error(ex.message ?: "잘못된 요청입니다.")
        val statusCode: HttpStatus = when (ex) {
            // domain
            is InvalidEmailFormatException -> HttpStatus.BAD_REQUEST
            is InvalidNameLengthException -> HttpStatus.BAD_REQUEST

            // application
            is AccountEmailExistsException -> HttpStatus.BAD_REQUEST
            is AccountNotFoundException -> HttpStatus.NOT_FOUND
            is BadCredentialsException -> HttpStatus.UNAUTHORIZED
            is IncorrectPasswordException -> HttpStatus.UNAUTHORIZED
            is InvalidRefreshTokenException -> HttpStatus.UNAUTHORIZED
            is BlankTokenException -> HttpStatus.BAD_REQUEST
            is InvalidTokenException -> HttpStatus.BAD_REQUEST
            is TokenExpiredException -> HttpStatus.UNAUTHORIZED

            // adapter/in
            is RefreshTokenCookieMissingException -> HttpStatus.UNAUTHORIZED

            else -> HttpStatus.BAD_REQUEST
        }

        return ResponseEntity
            .status(statusCode)
            .body(response)
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshTokenException(
        ex: InvalidRefreshTokenException,
    ): ResponseEntity<ApiResponse<Unit>> {
        log.error("[InvalidRefreshToken]: {}", ex.message, ex)

        val response = ApiResponse.error("유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요.")
        val clearedRefreshTokenCookie = cookieManager.clearCookie()

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .header(HttpHeaders.SET_COOKIE, clearedRefreshTokenCookie.toString())
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception,
    ): ResponseEntity<ApiResponse<Unit>> {
        log.error("[Exception]: {}", ex.message, ex)

        val response = ApiResponse.error("서버에 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
}