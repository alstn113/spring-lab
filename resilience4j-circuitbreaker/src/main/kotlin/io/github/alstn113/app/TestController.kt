package io.github.alstn113.app

import io.github.alstn113.resilience4j.core.exception.CallNotPermittedException
import io.github.alstn113.resilience4j.spring.annotation.CircuitBreaker
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class TestController {

    private val random = Random()

    // http://127.0.0.1:8080/dns?prob=0.4 로 반복 호출 시도
    @GetMapping("/dns")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallback")
    fun queryDns(@RequestParam prob: Double): String {

        return if (random.nextDouble() < prob) {
            "Cloudflare DNS 응답 성공 (1.1.1.1)"
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun fallback(prob: Double, e: CallNotPermittedException): String {
        return "Cloudflare DNS 차단됨 → Google DNS(8.8.8.8)로 우회"
    }

    private fun fallback(prob: Double, e: IllegalArgumentException): String {
        return "Cloudflare DNS 실패 → 요청 실패 응답"
    }
}
