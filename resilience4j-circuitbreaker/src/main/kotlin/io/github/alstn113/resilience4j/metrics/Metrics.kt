package io.github.alstn113.resilience4j.metrics

import java.util.concurrent.TimeUnit


interface Metrics {

    fun record(duration: Long, durationUnit: TimeUnit, outcome: Outcome): Snapshot
    fun getSnapshot(): Snapshot

    enum class Outcome {
        SUCCESS, ERROR
    }
}