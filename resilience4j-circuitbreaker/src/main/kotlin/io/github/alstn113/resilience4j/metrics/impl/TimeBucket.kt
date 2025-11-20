package io.github.alstn113.resilience4j.metrics.impl

class TimeBucket(
    private var epochSecond: Long,
) : BaseBucket() {

    fun reset(newEpochSecond: Long) {
        super.reset()
        epochSecond = newEpochSecond
    }

    fun getEpochSecond(): Long = epochSecond
}