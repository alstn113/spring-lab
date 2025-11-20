package io.github.alstn113.resilience4j.core.exception

import io.github.alstn113.resilience4j.core.CircuitBreaker

class CallNotPermittedException(
    message: String,
) : RuntimeException(message) {

    companion object {
        fun from(circuitBreaker: CircuitBreaker): CallNotPermittedException {
            val message = "CircuitBreaker '${circuitBreaker.getName()}'에 의해 호출이 차단되었습니다. " +
                    "현재 상태: ${circuitBreaker.getState()}"

            return CallNotPermittedException(message)
        }
    }
}