package io.github.alstn113.resilience4j.core

import io.github.alstn113.resilience4j.core.exception.CallNotPermittedException
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.TestConstructor
import java.time.Clock
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CircuitBreakerStateMachineTest(
    private val clock: Clock,
) {

    @Test
    fun `CLOSED 상태에서 실패율 임계치 초과 시 OPEN 상태로 전환`() {
        val config = CircuitBreakerConfig.Builder()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .permittedNumberOfCallsInHalfOpenState(2)
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                size = 3,
                minimumNumberOfCalls = 3
            )
            .build()
        val cb = CircuitBreakerStateMachine(
            name = "cb-closed-to-open",
            config = config,
            clock = clock,
        )

        assertEquals(CircuitBreaker.State.CLOSED, cb.getState())

        repeat(3) {
            try {
                cb.execute { throw RuntimeException("실패") }
            } catch (_: Exception) {
            }
        }

        assertEquals(CircuitBreaker.State.OPEN, cb.getState())
    }

    @Test
    fun `OPEN 상태에서 호출 시 CallNotPermittedException 발생`() {
        val config = CircuitBreakerConfig.Builder()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .permittedNumberOfCallsInHalfOpenState(2)
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                size = 3,
                minimumNumberOfCalls = 3
            )
            .build()
        val cb = CircuitBreakerStateMachine(
            name = "cb-open-state",
            config = config,
            clock = clock,
        )

        repeat(3) {
            try {
                cb.execute { throw RuntimeException("error") }
            } catch (_: Exception) {
            }
        }

        assertEquals(CircuitBreaker.State.OPEN, cb.getState())

        assertThrows<CallNotPermittedException> {
            cb.execute { "실행되지 않음" }
        }
    }

    @Test
    fun `OPEN 상태 waitDuration 지나면 HALF_OPEN 전환 후 성공 시 CLOSED 복구`() {
        val config = CircuitBreakerConfig.Builder()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofMillis(10))
            .permittedNumberOfCallsInHalfOpenState(2)
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                size = 3,
                minimumNumberOfCalls = 3
            )
            .build()

        val cb = CircuitBreakerStateMachine(
            name = "cb-open-to-half-open-to-closed",
            config = config,
            clock = clock,
        )

        repeat(3) {
            assertThrows<RuntimeException> {
                cb.execute { throw RuntimeException("fail") }
            }
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.getState())

        Thread.sleep(20)

        cb.execute { "success" } // half open call 1 (half open 으로 변경하고 호출)
        assertEquals(CircuitBreaker.State.HALF_OPEN, cb.getState())
        cb.execute { "success" } // half open call 2 -> closed

        assertEquals(CircuitBreaker.State.CLOSED, cb.getState())
    }


    @SpringBootApplication
    class TestApplication {
        @Bean
        fun clock(): Clock = Clock.systemUTC()
    }
}
