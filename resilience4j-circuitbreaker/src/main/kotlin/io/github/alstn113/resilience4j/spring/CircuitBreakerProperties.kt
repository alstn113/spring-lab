package io.github.alstn113.resilience4j.spring

import io.github.alstn113.resilience4j.core.CircuitBreakerConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("resilience4j.circuitbreaker")
data class CircuitBreakerProperties(
    val instances: Map<String, InstanceProperties> = emptyMap(),
) {

    data class InstanceProperties(
        val failureRateThreshold: Float = 50F,
        val permittedNumberOfCallsInHalfOpenState: Int = 10,
        val slidingWindow: SlidingWindowProperties = SlidingWindowProperties(),
        val waitDurationInOpenState: Long = 60000,
    )

    data class SlidingWindowProperties(
        val type: CircuitBreakerConfig.SlidingWindowType = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
        val size: Int = 100,
        val minimumNumberOfCalls: Int = 100,
    )
}
