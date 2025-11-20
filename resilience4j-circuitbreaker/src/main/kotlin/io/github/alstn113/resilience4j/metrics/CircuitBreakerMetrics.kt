package io.github.alstn113.resilience4j.metrics

import io.github.alstn113.resilience4j.core.CircuitBreakerConfig
import io.github.alstn113.resilience4j.metrics.impl.CountBasedMetrics
import io.github.alstn113.resilience4j.metrics.impl.TimeBasedMetrics
import java.time.Clock
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.LongAdder

class CircuitBreakerMetrics(
    private val slidingWindowType: CircuitBreakerConfig.SlidingWindowType,
    private val slidingWindowSize: Int,
    private val circuitBreakerConfig: CircuitBreakerConfig,
    private val clock: Clock,
) {

    private val metrics: Metrics
    private val failureRateThreshold: Float
    private val numberOfNotPermittedCalls: LongAdder
    private var minimumNumberOfCalls: Int

    init {
        when (slidingWindowType) {
            CircuitBreakerConfig.SlidingWindowType.COUNT_BASED -> {
                this.metrics = CountBasedMetrics(slidingWindowSize)
                this.minimumNumberOfCalls = minOf(circuitBreakerConfig.minimumNumberOfCalls, slidingWindowSize)
            }

            CircuitBreakerConfig.SlidingWindowType.TIME_BASED -> {
                this.metrics = TimeBasedMetrics(slidingWindowSize, clock)
                this.minimumNumberOfCalls = circuitBreakerConfig.minimumNumberOfCalls
            }
        }
        this.failureRateThreshold = circuitBreakerConfig.failureRateThreshold
        this.numberOfNotPermittedCalls = LongAdder()
    }

    fun onCallNotPermitted() {
        numberOfNotPermittedCalls.increment()
    }

    fun onSuccess(duration: Long, durationUnit: TimeUnit): Result {
        val snapshot = metrics.record(duration, durationUnit, Metrics.Outcome.SUCCESS)
        return checkIfThresholdsExceeded(snapshot)
    }

    fun onError(duration: Long, durationUnit: TimeUnit): Result {
        val snapshot = metrics.record(duration, durationUnit, Metrics.Outcome.ERROR)
        return checkIfThresholdsExceeded(snapshot)
    }

    fun getFailureRate(): Float {
        val snapshot = metrics.getSnapshot()
        return snapshot.failureRate
    }

    fun getNumberOfNotPermittedCalls(): Long {
        return numberOfNotPermittedCalls.sum()
    }

    fun getNumberOfFailedCalls(): Int {
        val snapshot = metrics.getSnapshot()
        return snapshot.numberOfFailedCalls
    }

    fun getNumberOfSuccessfulCalls(): Int {
        val snapshot = metrics.getSnapshot()
        return snapshot.numberOfSuccessfulCalls
    }

    fun getTotalNumberOfCalls(): Int {
        val snapshot = metrics.getSnapshot()
        return snapshot.totalNumberOfCalls
    }

    private fun checkIfThresholdsExceeded(snapshot: Snapshot): Result {
        return when {
            snapshot.totalNumberOfCalls < minimumNumberOfCalls -> Result.BELOW_MINIMUM_CALLS_THRESHOLD
            snapshot.failureRate >= failureRateThreshold -> Result.FAILURE_RATE_ABOVE_THRESHOLD
            else -> Result.BELOW_THRESHOLD
        }
    }

    companion object {
        fun forClosed(
            circuitBreakerConfig: CircuitBreakerConfig,
            clock: Clock,
        ): CircuitBreakerMetrics {
            return CircuitBreakerMetrics(
                slidingWindowType = circuitBreakerConfig.slidingWindowType,
                slidingWindowSize = circuitBreakerConfig.slidingWindowSize,
                circuitBreakerConfig = circuitBreakerConfig,
                clock = clock,
            )
        }

        fun forHalfOpen(
            permittedNumberOfCallsInHalfOpenState: Int,
            circuitBreakerConfig: CircuitBreakerConfig,
            clock: Clock,
        ): CircuitBreakerMetrics {
            return CircuitBreakerMetrics(
                slidingWindowType = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                slidingWindowSize = permittedNumberOfCallsInHalfOpenState,
                circuitBreakerConfig = circuitBreakerConfig,
                clock = clock,
            )
        }
    }

    enum class Result {
        BELOW_THRESHOLD, // 실패율이 임계값 이하임. (정상 작동)
        FAILURE_RATE_ABOVE_THRESHOLD, // 실패율이 임계값을 넘음.
        BELOW_MINIMUM_CALLS_THRESHOLD, // 데이터가 충분히 쌓이지 않음.
        ;
    }
}