package io.github.alstn113.rtr.adapter.`in`.web.controller

import io.github.alstn113.rtr.adapter.`in`.web.common.ApiResponse
import io.github.alstn113.rtr.adapter.`in`.web.security.AccountPrincipal
import io.github.alstn113.rtr.adapter.`in`.web.security.resolver.AuthenticationPrincipal
import io.github.alstn113.rtr.application.port.`in`.GetProfileUseCase
import io.github.alstn113.rtr.application.port.`in`.dto.AccountProfile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val getProfileUseCase: GetProfileUseCase,
) {

    @GetMapping("/accounts/profile")
    fun getProfile(
        @AuthenticationPrincipal principal: AccountPrincipal,
    ): ResponseEntity<ApiResponse<AccountProfile>> {
        val profile = getProfileUseCase(accountId = principal.accountId)

        return ResponseEntity.ok(ApiResponse.success(profile))
    }
}