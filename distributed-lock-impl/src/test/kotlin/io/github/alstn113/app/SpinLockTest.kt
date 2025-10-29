package io.github.alstn113.app

import io.github.alstn113.app.distributedlock.spinlock.SpinLock
import io.github.alstn113.app.distributedlock.withLock
import io.github.alstn113.app.support.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class SpinLockTest(
    private val spinLock: SpinLock,
) : AbstractIntegrationTest() {

    @Test
    fun `여러 스레드가 동시에 락을 획득하려고 시도할 때, 스핀락이 올바르게 동작해야 한다`() {
        val threadCount = 10
        val executorService = Executors.newFixedThreadPool(threadCount)
        val startGate = CountDownLatch(1)
        val endGate = CountDownLatch(threadCount)
        val counter = AtomicInteger(0)

        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    startGate.await()

                    spinLock.withLock(
                        key = "test-lock-key",
                        waitTime = Duration.ofSeconds(3),
                        leaseTime = Duration.ofSeconds(3),
                    ) {
                        val currentValue = counter.get()
                        Thread.sleep(1)
                        counter.set(currentValue + 1)
                    }

                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: IllegalStateException) {
                    println("락 획득 실패: ${e.message}")
                } finally {
                    endGate.countDown()
                }
            }
        }

        startGate.countDown()
        endGate.await()
        executorService.shutdown()

        println("최종 카운터 값: ${counter.get()}")
        assertEquals(threadCount, counter.get())
    }

    @Test
    fun `재진입이 가능해야 한다`() {
        val lockKey = "reentrant-lock-key"
        val result = mutableListOf<String>()

        spinLock.withLock(
            key = lockKey,
            waitTime = Duration.ofSeconds(3),
            leaseTime = Duration.ofSeconds(3),
        ) {
            result.add("A1")

            spinLock.withLock(
                key = lockKey,
                waitTime = Duration.ofSeconds(3),
                leaseTime = Duration.ofSeconds(3),
            ) {
                result.add("A2")
            }
        }

        println("재진입 결과: $result")
        assertEquals(listOf("A1", "A2"), result)
    }
}
