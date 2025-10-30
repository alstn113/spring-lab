package io.github.alstn113.app.distributedlock

import java.time.Duration

interface DistributedLock {

    fun tryLock(key: String, waitTime: Duration, leaseTime: Duration): Boolean
    fun unlock(key: String)
}

// 분산락을 사용할 수 있는 확장 함수
fun DistributedLock.withLock(
    key: String,
    waitTime: Duration,
    leaseTime: Duration,
    callback: () -> Unit,
) {
    val acquired = this.tryLock(key, waitTime, leaseTime)
    if (!acquired) {
        throw IllegalStateException("Failed to acquire lock for key: $key")
    }

    try {
        callback()
    } finally {
        this.unlock(key)
    }
}