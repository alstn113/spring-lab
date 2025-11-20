package io.github.alstn113.resilience4j.metrics

import io.github.alstn113.resilience4j.metrics.impl.TotalBucket
import java.time.Duration

class Snapshot(
    private val totalBucket: TotalBucket,
) {

    val totalDuration: Duration = Duration.ofMillis(totalBucket.totalDurationMillis)
    val totalNumberOfCalls: Int = totalBucket.numberOfCalls
    val numberOfSuccessfulCalls: Int = totalBucket.numberOfCalls - totalBucket.numberOfFailedCalls
    val numberOfFailedCalls: Int = totalBucket.numberOfFailedCalls
    val failureRate: Float = if (totalBucket.numberOfCalls == 0) {
        0.0f
    } else {
        totalBucket.numberOfFailedCalls * 100.0f / totalBucket.numberOfCalls
    }
}