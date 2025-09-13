package io.github.alstn113.rtr.adapter.`in`.web.controller

import io.github.alstn113.rtr.adapter.`in`.web.common.ApiResponse
import io.github.alstn113.rtr.adapter.`in`.web.security.AccountPrincipal
import io.github.alstn113.rtr.adapter.`in`.web.security.resolver.AuthenticationPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/test")
    fun test(
        @AuthenticationPrincipal principal: AccountPrincipal,
    ): ResponseEntity<ApiResponse<String>> {
        val message = if (principal.isAnonymous()) {
            "안녕하세요! 당신은 익명 사용자입니다."
        } else {
            "안녕하세요! 사용자(${principal.accountId})님 환영합니다."
        }

        return ResponseEntity.ok(ApiResponse.success(message))
    }
}