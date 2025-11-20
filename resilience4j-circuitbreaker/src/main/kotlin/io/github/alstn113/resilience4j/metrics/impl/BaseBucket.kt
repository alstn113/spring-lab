package io.github.alstn113.resilience4j.metrics.impl

import io.github.alstn113.resilience4j.metrics.Metrics
import java.util.concurrent.TimeUnit

abstract class BaseBucket {

    var totalDurationMillis: Long = 0
    var numberOfCalls: Int = 0
    var numberOfFailedCalls: Int = 0

    fun record(duration: Long, durationUnit: TimeUnit, outcome: Metrics.Outcome) {
        this.numberOfCalls++
        this.totalDurationMillis += durationUnit.toMillis(duration)

        when (outcome) {
            Metrics.Outcome.SUCCESS -> {
                // no-op
            }

            Metrics.Outcome.ERROR -> {
                this.numberOfFailedCalls++
            }
        }
    }

    fun reset() {
        this.totalDurationMillis = 0
        this.numberOfCalls = 0
        this.numberOfFailedCalls = 0
    }
}