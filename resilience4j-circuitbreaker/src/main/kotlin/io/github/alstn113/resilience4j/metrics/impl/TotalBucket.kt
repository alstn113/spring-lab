package io.github.alstn113.resilience4j.metrics.impl

class TotalBucket : BaseBucket() {

    fun removeBucket(bucket: BaseBucket) {
        this.totalDurationMillis -= bucket.totalDurationMillis
        this.numberOfCalls -= bucket.numberOfCalls
        this.numberOfFailedCalls -= bucket.numberOfFailedCalls
    }
}