package io.github.alstn113.resilience4j.core

import java.time.Duration

class CircuitBreakerConfig private constructor(
    val failureRateThreshold: Float,
    val permittedNumberOfCallsInHalfOpenState: Int,
    val waitDurationInOpenState: Duration,
    val slidingWindowType: SlidingWindowType,
    val slidingWindowSize: Int,
    val minimumNumberOfCalls: Int,
) {

    class Builder {
        private var failureRateThreshold = DEFAULT_FAILURE_RATE_THRESHOLD
        private var permittedNumberOfCallsInHalfOpenState = DEFAULT_PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_STATE
        private var waitDurationInOpenState = Duration.ofMillis(DEFAULT_WAIT_DURATION_IN_OPEN_STATE_MS)
        private var slidingWindowType = DEFAULT_SLIDING_WINDOW_TYPE
        private var slidingWindowSize = DEFAULT_SLIDING_WINDOW_SIZE
        private var minimumNumberOfCalls = DEFAULT_MINIMUM_NUMBER_OF_CALLS

        fun failureRateThreshold(threshold: Float) = apply {
            require(threshold in 0.0..100.0) {
                "failureRateThreshold는 0.0에서 100.0 사이여야 합니다."
            }
            this.failureRateThreshold = threshold
        }

        fun permittedNumberOfCallsInHalfOpenState(number: Int) = apply {
            require(number > 0) {
                "permittedNumberOfCallsInHalfOpenState는 0보다 커야 합니다."
            }
            this.permittedNumberOfCallsInHalfOpenState = number
        }

        fun waitDurationInOpenState(duration: Duration) = apply {
            require(!duration.isNegative && !duration.isZero) {
                "waitDurationInOpenState는 0보다 커야 합니다."
            }
            this.waitDurationInOpenState = duration
        }

        fun slidingWindow(
            type: SlidingWindowType,
            size: Int,
            minimumNumberOfCalls: Int,
        ) = apply {
            require(size > 0) {
                "slidingWindowSize는 0보다 커야 합니다."
            }
            require(minimumNumberOfCalls > 0) {
                "minimumNumberOfCalls는 0보다 커야 합니다."
            }
            if (type == SlidingWindowType.COUNT_BASED) {
                this.minimumNumberOfCalls = minOf(size, minimumNumberOfCalls)
            } else {
                this.minimumNumberOfCalls = minimumNumberOfCalls
            }
            this.slidingWindowType = type
            this.slidingWindowSize = size
        }

        fun build(): CircuitBreakerConfig {
            return CircuitBreakerConfig(
                failureRateThreshold = failureRateThreshold,
                permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState,
                waitDurationInOpenState = waitDurationInOpenState,
                slidingWindowType = slidingWindowType,
                slidingWindowSize = slidingWindowSize,
                minimumNumberOfCalls = minimumNumberOfCalls,
            )
        }
    }

    enum class SlidingWindowType {
        COUNT_BASED,
        TIME_BASED
    }

    companion object {
        fun custom() = Builder()

        private const val DEFAULT_FAILURE_RATE_THRESHOLD = 50F
        private const val DEFAULT_PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_STATE = 10
        private const val DEFAULT_WAIT_DURATION_IN_OPEN_STATE_MS = 60_000L
        private val DEFAULT_SLIDING_WINDOW_TYPE = SlidingWindowType.COUNT_BASED
        private const val DEFAULT_SLIDING_WINDOW_SIZE = 100
        private const val DEFAULT_MINIMUM_NUMBER_OF_CALLS = 100
    }
}