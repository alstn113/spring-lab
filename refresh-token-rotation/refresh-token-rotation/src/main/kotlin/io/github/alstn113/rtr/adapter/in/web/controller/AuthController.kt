package io.github.alstn113.rtr.adapter.`in`.web.controller

import io.github.alstn113.rtr.adapter.`in`.web.common.ApiResponse
import io.github.alstn113.rtr.adapter.`in`.web.controller.dto.*
import io.github.alstn113.rtr.adapter.`in`.web.controller.exception.RefreshTokenCookieMissingException
import io.github.alstn113.rtr.adapter.`in`.web.security.AccountPrincipal
import io.github.alstn113.rtr.adapter.`in`.web.security.cookie.RefreshTokenCookieManager
import io.github.alstn113.rtr.adapter.`in`.web.security.resolver.AuthenticationPrincipal
import io.github.alstn113.rtr.application.port.`in`.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val reissueTokensUseCase: ReissueTokensUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val logoutAllUseCase: LogoutAllUseCase,
    private val cookieManager: RefreshTokenCookieManager,
) {

    @PostMapping("/auth/sign-up")
    fun signUp(
        @RequestBody request: SignUpRequest,
    ): ResponseEntity<ApiResponse<SignUpResponse>> {
        val command = request.toCommand()
        val result = signUpUseCase(command)

        val response = SignUpResponse(
            accountId = result.accountId,
            email = result.email,
            name = result.name,
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/auth/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val command = request.toCommand()
        val result = loginUseCase(command)

        val refreshTokenCookie = cookieManager.createCookie(result.refreshToken)
        val response = LoginResponse(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponse.success(response))
    }

    @PostMapping("/auth/change-password")
    fun changePassword(
        @AuthenticationPrincipal principal: AccountPrincipal,
        @RequestBody request: ChangePasswordRequest,
    ): ResponseEntity<ApiResponse<String>> {
        val command = request.toCommand(accountId = principal.accountId)
        changePasswordUseCase(command)
        val clearedRefreshTokenCookie = cookieManager.clearCookie()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearedRefreshTokenCookie.toString())
            .body(ApiResponse.success("비밀번호가 변경되었습니다. 다시 로그인 해주세요."))
    }

    @PostMapping("/auth/refresh")
    fun refresh(
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<RefreshTokenResponse>> {
        val refreshToken = cookieManager.extractToken(request)
            ?: throw RefreshTokenCookieMissingException()
        val result = reissueTokensUseCase(refreshToken = refreshToken)

        val refreshTokenCookie = cookieManager.createCookie(result.refreshToken)
        val response = RefreshTokenResponse(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponse.success(response))
    }

    @PostMapping("/auth/logout")
    fun logout(
        @AuthenticationPrincipal principal: AccountPrincipal,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<String>> {
        val refreshToken = cookieManager.extractToken(request)
        logoutUseCase(refreshToken = refreshToken)
        val clearedRefreshTokenCookie = cookieManager.clearCookie()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearedRefreshTokenCookie.toString())
            .body(ApiResponse.success("로그아웃 되었습니다."))
    }

    @PostMapping("/auth/logout/all")
    fun logoutAll(
        @AuthenticationPrincipal principal: AccountPrincipal,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<String>> {
        logoutAllUseCase(accountId = principal.accountId)
        val clearedRefreshTokenCookie = cookieManager.clearCookie()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearedRefreshTokenCookie.toString())
            .body(ApiResponse.success("모든 기기에서 로그아웃 되었습니다."))
    }
}
