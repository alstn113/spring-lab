package io.github.alstn113.resilience4j.core

import java.util.concurrent.TimeUnit


interface CircuitBreaker {

    fun onSuccess(duration: Long, durationUnit: TimeUnit)
    fun onError(duration: Long, durationUnit: TimeUnit, throwable: Throwable)

    fun tryAcquirePermission(): Boolean
    fun acquirePermission()
    fun getState(): State
    fun getName(): String

    enum class State {
        CLOSED, OPEN, HALF_OPEN
    }

    enum class StateTransition(
        private val fromState: State,
        private val toState: State,
    ) {
        CLOSED_TO_OPEN(State.CLOSED, State.OPEN),
        OPEN_TO_HALF_OPEN(State.OPEN, State.HALF_OPEN),
        HALF_OPEN_TO_CLOSED(State.HALF_OPEN, State.CLOSED),
        HALF_OPEN_TO_OPEN(State.HALF_OPEN, State.OPEN)
        ;

        companion object {
            private val STATE_TRANSITION_MAP: Map<Pair<State, State>, StateTransition> =
                entries.associateBy { it.fromState to it.toState }

            fun transitionBetween(name: String, fromState: State, toState: State): StateTransition {
                val transition = STATE_TRANSITION_MAP[fromState to toState]
                require(transition != null) { "유효하지 않은 상태 전이입니다: $name from=$fromState to=$toState" }
                return transition
            }
        }
    }
}

fun <T> CircuitBreaker.execute(block: () -> T): T {
    this.acquirePermission()

    val start = System.nanoTime()

    try {
        val result = block()

        val duration = System.nanoTime() - start
        this.onSuccess(duration, TimeUnit.NANOSECONDS)
        return result
    } catch (e: Exception) {
        val duration = System.nanoTime() - start
        this.onError(duration, TimeUnit.NANOSECONDS, e)
        throw e
    }
}