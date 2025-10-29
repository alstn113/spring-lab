package io.github.alstn113.app.distributedlock

import java.time.Duration

interface DistributedLock {

    fun tryLock(key: String, waitTime: Duration, leaseTime: Duration): Boolean
    fun unlock(key: String)
}

fun DistributedLock.withLock(
    key: String,
    waitTime: Duration,
    leaseTime: Duration,
    callback: () -> Unit,
) {
    val locked = this.tryLock(key, waitTime, leaseTime)
    if (!locked) {
        throw IllegalStateException("Failed to acquire lock for key: $key")
    }

    try {
        callback()
    } finally {
        this.unlock(key)
    }
}