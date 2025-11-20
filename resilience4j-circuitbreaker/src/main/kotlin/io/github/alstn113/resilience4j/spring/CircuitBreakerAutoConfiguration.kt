package io.github.alstn113.resilience4j.spring

import io.github.alstn113.resilience4j.core.CircuitBreakerConfig
import io.github.alstn113.resilience4j.core.CircuitBreakerRegistry
import io.github.alstn113.resilience4j.spring.aop.CircuitBreakerAspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.Duration

@Configuration
@EnableConfigurationProperties(CircuitBreakerProperties::class)
class CircuitBreakerAutoConfiguration {

    @Bean
    fun circuitBreakerRegistry(
        properties: CircuitBreakerProperties,
        clock: Clock,
    ): CircuitBreakerRegistry {

        if (properties.instances.isEmpty()) {
            return CircuitBreakerRegistry(
                clock = clock,
                defaultConfig = CircuitBreakerConfig.custom().build()
            )
        }

        val map = properties.instances.mapValues { (_, p) ->
            CircuitBreakerConfig.custom()
                .failureRateThreshold(p.failureRateThreshold)
                .permittedNumberOfCallsInHalfOpenState(p.permittedNumberOfCallsInHalfOpenState)
                .slidingWindow(
                    p.slidingWindow.type,
                    p.slidingWindow.size,
                    p.slidingWindow.minimumNumberOfCalls
                )
                .waitDurationInOpenState(Duration.ofMillis(p.waitDurationInOpenState))
                .build()
        }

        return CircuitBreakerRegistry(configMap = map, clock = clock)
    }

    @Bean
    @ConditionalOnMissingBean
    fun circuitBreakerAspect(registry: CircuitBreakerRegistry): CircuitBreakerAspect {
        return CircuitBreakerAspect(registry)
    }

    @Bean
    @ConditionalOnMissingBean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}
