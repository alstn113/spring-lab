package io.github.alstn113.resilience4j.core

import io.github.alstn113.resilience4j.core.exception.CallNotPermittedException
import io.github.alstn113.resilience4j.metrics.CircuitBreakerMetrics
import io.github.alstn113.resilience4j.metrics.CircuitBreakerMetrics.Result.BELOW_THRESHOLD
import io.github.alstn113.resilience4j.metrics.CircuitBreakerMetrics.Result.FAILURE_RATE_ABOVE_THRESHOLD
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class CircuitBreakerStateMachine(
    private val name: String,
    private val config: CircuitBreakerConfig,
    private val clock: Clock,
) : CircuitBreaker {

    private val log = LoggerFactory.getLogger(javaClass)
    private val stateRef: AtomicReference<CircuitBreakerState> = AtomicReference(ClosedState())

    override fun tryAcquirePermission(): Boolean {
        return stateRef.get().tryAcquirePermission()
    }

    override fun acquirePermission() {
        try {
            stateRef.get().acquirePermission()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onSuccess(duration: Long, durationUnit: TimeUnit) {
        stateRef.get().onSuccess(duration, durationUnit)
    }

    override fun onError(duration: Long, durationUnit: TimeUnit, throwable: Throwable) {
        stateRef.get().onError(duration, durationUnit)
    }

    override fun getState(): CircuitBreaker.State {
        return stateRef.get().getState()
    }

    override fun getName(): String {
        return name
    }

    private fun stateTransition(
        newState: CircuitBreaker.State,
        newStateGenerator: (curState: CircuitBreakerState) -> CircuitBreakerState,
    ) {
        val prevState = stateRef.getAndUpdate { curState ->
            CircuitBreaker.StateTransition.Companion.transitionBetween(
                name = name,
                fromState = curState.getState(),
                toState = newState
            )

            newStateGenerator(curState)
        }

        log.info("CircuitBreaker '$name' 상태 전이: ${prevState.getState()} -> $newState")
    }

    private interface CircuitBreakerState {
        fun tryAcquirePermission(): Boolean
        fun acquirePermission()
        fun onSuccess(duration: Long, durationUnit: TimeUnit)
        fun onError(duration: Long, durationUnit: TimeUnit)
        fun getState(): CircuitBreaker.State
        fun getMetrics(): CircuitBreakerMetrics
    }

    private inner class ClosedState : CircuitBreakerState {

        private val circuitBreakerMetrics = CircuitBreakerMetrics.forClosed(config, clock)
        private val isClosed = AtomicBoolean(true)

        override fun tryAcquirePermission(): Boolean {
            return isClosed.get()
        }

        override fun acquirePermission() {
            // no-op
        }

        override fun onSuccess(duration: Long, durationUnit: TimeUnit) {
            checkIfThresholdExceeded(circuitBreakerMetrics.onSuccess(duration, durationUnit))
        }

        override fun onError(duration: Long, durationUnit: TimeUnit) {
            checkIfThresholdExceeded(circuitBreakerMetrics.onError(duration, durationUnit))
        }

        override fun getState(): CircuitBreaker.State {
            return CircuitBreaker.State.CLOSED
        }

        override fun getMetrics(): CircuitBreakerMetrics {
            return circuitBreakerMetrics
        }

        private fun checkIfThresholdExceeded(result: CircuitBreakerMetrics.Result) {
            if (result == FAILURE_RATE_ABOVE_THRESHOLD && isClosed.compareAndSet(true, false)) {
                stateTransition(
                    newState = CircuitBreaker.State.OPEN,
                    newStateGenerator = { curState -> OpenState(curState.getMetrics()) }
                )
            }
        }
    }

    private inner class OpenState(
        private val circuitBreakerMetrics: CircuitBreakerMetrics,
    ) : CircuitBreakerState {

        private val isOpen = AtomicBoolean(true)
        private val retryAfterWaitDuration: Instant = clock.instant().plus(config.waitDurationInOpenState)

        /**
         * Open 상태에서는 설정된 대기 시간 이후에 대해서 Half-Open 상태로 전환을 시도합니다.
         */
        override fun tryAcquirePermission(): Boolean {
            if (clock.instant().isAfter(retryAfterWaitDuration)) {
                toHalfOpenState()
                val callPermitted = stateRef.get().tryAcquirePermission()
                if (!callPermitted) {
                    circuitBreakerMetrics.onCallNotPermitted()
                }
                return callPermitted
            }
            circuitBreakerMetrics.onCallNotPermitted()
            return false
        }

        override fun acquirePermission() {
            if (!tryAcquirePermission()) {
                throw CallNotPermittedException.from(circuitBreaker = this@CircuitBreakerStateMachine)
            }
        }

        override fun onSuccess(duration: Long, durationUnit: TimeUnit) {
            circuitBreakerMetrics.onSuccess(duration, durationUnit)
        }

        override fun onError(duration: Long, durationUnit: TimeUnit) {
            circuitBreakerMetrics.onError(duration, durationUnit)
        }

        override fun getState(): CircuitBreaker.State {
            return CircuitBreaker.State.OPEN
        }

        override fun getMetrics(): CircuitBreakerMetrics {
            return circuitBreakerMetrics
        }

        @Synchronized
        private fun toHalfOpenState() {
            if (isOpen.compareAndSet(true, false)) {
                stateTransition(
                    newState = CircuitBreaker.State.HALF_OPEN,
                    newStateGenerator = { HalfOpenState() }
                )
            }
        }
    }

    private inner class HalfOpenState : CircuitBreakerState {

        private val permittedNumberOfCallsInHalfState = config.permittedNumberOfCallsInHalfOpenState
        private val permittedNumberOfCalls = AtomicInteger(permittedNumberOfCallsInHalfState)
        private val circuitBreakerMetrics = CircuitBreakerMetrics.forHalfOpen(
            permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfState,
            circuitBreakerConfig = config,
            clock = clock,
        )
        private val isHalfOpen = AtomicBoolean(true)

        /**
         * Half-Open 상태에서는 제한된 수의 호출만 허용합니다.
         */
        override fun tryAcquirePermission(): Boolean {
            val previousValue = permittedNumberOfCalls.getAndUpdate { current ->
                if (current > 0) current - 1 else 0
            }

            if (previousValue > 0) {
                return true
            }

            circuitBreakerMetrics.onCallNotPermitted()
            return false
        }

        override fun acquirePermission() {
            if (!tryAcquirePermission()) {
                throw CallNotPermittedException.from(circuitBreaker = this@CircuitBreakerStateMachine)
            }
        }

        override fun onSuccess(duration: Long, durationUnit: TimeUnit) {
            checkIfThresholdExceeded(circuitBreakerMetrics.onSuccess(duration, durationUnit))
        }

        override fun onError(duration: Long, durationUnit: TimeUnit) {
            checkIfThresholdExceeded(circuitBreakerMetrics.onError(duration, durationUnit))
        }

        override fun getState(): CircuitBreaker.State {
            return CircuitBreaker.State.HALF_OPEN
        }

        override fun getMetrics(): CircuitBreakerMetrics {
            return circuitBreakerMetrics
        }

        private fun checkIfThresholdExceeded(result: CircuitBreakerMetrics.Result) {
            if (result == FAILURE_RATE_ABOVE_THRESHOLD && isHalfOpen.compareAndSet(true, false)) {
                stateTransition(
                    newState = CircuitBreaker.State.OPEN,
                    newStateGenerator = { curState -> OpenState(curState.getMetrics()) }
                )
            } else if (result == BELOW_THRESHOLD && isHalfOpen.compareAndSet(true, false)) {
                stateTransition(
                    newState = CircuitBreaker.State.CLOSED,
                    newStateGenerator = { ClosedState() }
                )
            }
        }
    }
}