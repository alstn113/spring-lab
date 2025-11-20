package io.github.alstn113.resilience4j.spring

import io.github.alstn113.resilience4j.spring.annotation.CircuitBreaker
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.test.context.TestConstructor
import kotlin.test.assertEquals


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CircuitBreakerAspectTest(
    private val testService: TestService,
) {

    @Test
    fun `성공 시 원래 메서드 호출 테스트`() {
        val result = testService.operation {
            "정상 처리됨"
        }

        assertEquals("정상 처리됨", result)
    }

    @Test
    fun `실패 시 fallback 호출 테스트`() {
        val result = testService.operation {
            throw RuntimeException("의도적 예외 발생")
        }

        assertEquals("fallback 호출됨", result)
    }

    @Test
    fun `처리할 수 있는 예외 타입이 없는 경우 상위 타입을 찾는다`() {
        val result = testService.operation {
            throw NumberFormatException("상위 타입 Exception을 찾는다")
        }

        assertEquals("fallback 호출됨", result)
    }

    @SpringBootApplication
    class TestApplication {
        @Bean
        fun testService() = TestService()
    }

    @Service
    class TestService {

        @CircuitBreaker(name = "test", fallbackMethod = "fallback")
        fun operation(block: () -> String): String {
            return block()
        }

        fun fallback(block: () -> String, e: Exception): String {
            return "fallback 호출됨"
        }
    }
}