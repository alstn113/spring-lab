package io.github.alstn113.resilience4j.metrics.impl

import io.github.alstn113.resilience4j.metrics.Metrics
import io.github.alstn113.resilience4j.metrics.Snapshot
import java.util.concurrent.TimeUnit

class CountBasedMetrics(
    private val windowSize: Int,
) : Metrics {

    private val buckets: Array<CountBucket> = Array(windowSize) { CountBucket() }
    private val total: TotalBucket = TotalBucket()
    private var headIndex: Int = 0

    @Synchronized
    override fun record(
        duration: Long,
        durationUnit: TimeUnit,
        outcome: Metrics.Outcome,
    ): Snapshot {
        total.record(duration, durationUnit, outcome)
        val bucket = advanceWindow()
        bucket.record(duration, durationUnit, outcome)

        return Snapshot(total)
    }

    @Synchronized
    override fun getSnapshot(): Snapshot = Snapshot(total)

    private fun advanceWindow(): CountBucket {
        moveHeadIndex()
        val bucket = buckets[headIndex]
        total.removeBucket(bucket)
        bucket.reset()
        return bucket
    }

    private fun moveHeadIndex() {
        this.headIndex = (headIndex + 1) % windowSize
    }
}