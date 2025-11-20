package io.github.alstn113.resilience4j.core

import java.time.Clock
import java.util.concurrent.ConcurrentHashMap

class CircuitBreakerRegistry(
    private val defaultConfig: CircuitBreakerConfig,
    private val clock: Clock,
) {

    private val entryMap = ConcurrentHashMap<String, CircuitBreaker>()

    constructor(
        configMap: Map<String, CircuitBreakerConfig>,
        defaultConfig: CircuitBreakerConfig = CircuitBreakerConfig.custom().build(),
        clock: Clock,
    ) : this(defaultConfig = defaultConfig, clock = clock) {
        configMap.forEach { (name, config) ->
            entryMap[name] = CircuitBreakerStateMachine(
                name = name,
                config = config,
                clock = clock,
            )
        }
    }

    fun circuitBreaker(
        name: String,
        config: CircuitBreakerConfig = defaultConfig,
    ): CircuitBreaker {
        return entryMap.computeIfAbsent(name) {
            CircuitBreakerStateMachine(
                name = name,
                config = config,
                clock = clock,
            )
        }
    }
}