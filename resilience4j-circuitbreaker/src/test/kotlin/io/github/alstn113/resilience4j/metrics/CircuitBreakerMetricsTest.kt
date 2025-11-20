package io.github.alstn113.resilience4j.metrics

import io.github.alstn113.resilience4j.core.CircuitBreakerConfig
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class CircuitBreakerMetricsTest {

    private val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())

    @Test
    fun `카운트 기반 metrics 기본 기록과 임계치 확인`() {
        val config = CircuitBreakerConfig.Builder()
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                size = 5,
                minimumNumberOfCalls = 3
            )
            .failureRateThreshold(50f)
            .build()

        val metrics = CircuitBreakerMetrics.forClosed(config, clock)

        metrics.onSuccess(100, TimeUnit.MILLISECONDS)
        metrics.onError(200, TimeUnit.MILLISECONDS)
        val result = metrics.onError(150, TimeUnit.MILLISECONDS)

        assertEquals(CircuitBreakerMetrics.Result.FAILURE_RATE_ABOVE_THRESHOLD, result)
        assertEquals(3, metrics.getTotalNumberOfCalls())
        assertEquals(2, metrics.getNumberOfFailedCalls())
        assertEquals(1, metrics.getNumberOfSuccessfulCalls())
    }

    @Test
    fun `카운트 기반 metrics 최소 호출 수 미만`() {
        val config = CircuitBreakerConfig.Builder()
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                size = 5,
                minimumNumberOfCalls = 3
            )
            .failureRateThreshold(50f)
            .build()

        val metrics = CircuitBreakerMetrics.forClosed(config, clock)

        metrics.onSuccess(100, TimeUnit.MILLISECONDS)
        val result = metrics.onError(100, TimeUnit.MILLISECONDS)

        assertEquals(CircuitBreakerMetrics.Result.BELOW_MINIMUM_CALLS_THRESHOLD, result)
    }

    @Test
    fun `시간 기반 metrics 슬라이딩 윈도우 테스트`() {
        val config = CircuitBreakerConfig.Builder()
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.TIME_BASED,
                size = 5,
                minimumNumberOfCalls = 3
            )
            .failureRateThreshold(50f)
            .build()

        var mutableClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val metrics = CircuitBreakerMetrics.forClosed(config, mutableClock)

        metrics.onSuccess(100, TimeUnit.MILLISECONDS)
        metrics.onError(200, TimeUnit.MILLISECONDS)
        assertEquals(2, metrics.getTotalNumberOfCalls())

        mutableClock = Clock.offset(mutableClock, java.time.Duration.ofSeconds(10))
        val metricsWithOffset = CircuitBreakerMetrics(
            CircuitBreakerConfig.SlidingWindowType.TIME_BASED,
            config.slidingWindowSize,
            config,
            mutableClock
        )

        metricsWithOffset.onSuccess(50, TimeUnit.MILLISECONDS)
        assertEquals(1, metricsWithOffset.getTotalNumberOfCalls())
    }

    @Test
    fun `허용되지 않은 호출 수 카운트 확인`() {
        val config = CircuitBreakerConfig.Builder()
            .slidingWindow(
                type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                size = 5,
                minimumNumberOfCalls = 3
            )
            .failureRateThreshold(50f)
            .build()

        val metrics = CircuitBreakerMetrics.forClosed(config, clock)
        repeat(4) { metrics.onCallNotPermitted() }

        assertEquals(4, metrics.getNumberOfNotPermittedCalls())
    }
}
