package io.github.alstn113.resilience4j.metrics.impl

import io.github.alstn113.resilience4j.metrics.Metrics
import io.github.alstn113.resilience4j.metrics.Snapshot
import java.time.Clock
import java.util.concurrent.TimeUnit
import kotlin.math.min

class TimeBasedMetrics(
    private val windowSizeInSeconds: Int,
    private val clock: Clock,
) : Metrics {

    private val buckets: Array<TimeBucket>
    private val total: TotalBucket = TotalBucket()
    private var headIndex: Int = 0

    init {
        var now = clock.instant().epochSecond
        this.buckets = Array(windowSizeInSeconds) { TimeBucket(now++) }
    }

    @Synchronized
    override fun record(duration: Long, durationUnit: TimeUnit, outcome: Metrics.Outcome): Snapshot {
        total.record(duration, durationUnit, outcome)
        val bucket = advanceWindowToCurrentEpoch()
        bucket.record(duration, durationUnit, outcome)
        return Snapshot(total)
    }

    @Synchronized
    override fun getSnapshot(): Snapshot {
        advanceWindowToCurrentEpoch()
        return Snapshot(total)
    }

    private fun advanceWindowToCurrentEpoch(): TimeBucket {
        var currentBucket = buckets[headIndex]
        val now = clock.instant().epochSecond
        val delta = now - currentBucket.getEpochSecond()

        if (delta == 0L) return currentBucket

        var bucketsToMove = min(delta, windowSizeInSeconds.toLong())
        while (bucketsToMove > 0) {
            bucketsToMove--
            moveHeadIndex()
            currentBucket = buckets[headIndex]
            total.removeBucket(currentBucket)
            currentBucket.reset(now - bucketsToMove)
        }
        return currentBucket
    }

    private fun moveHeadIndex() {
        headIndex = (headIndex + 1) % windowSizeInSeconds
    }
}