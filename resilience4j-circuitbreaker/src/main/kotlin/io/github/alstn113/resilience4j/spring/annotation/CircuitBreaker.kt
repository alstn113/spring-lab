package io.github.alstn113.resilience4j.spring.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CircuitBreaker(
    val name: String,
    val fallbackMethod: String = "",
)