package io.github.alstn113.resilience4j.spring.aop

import io.github.alstn113.resilience4j.core.CircuitBreakerRegistry
import io.github.alstn113.resilience4j.core.execute
import io.github.alstn113.resilience4j.spring.annotation.CircuitBreaker
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

@Aspect
class CircuitBreakerAspect(
    private val registry: CircuitBreakerRegistry,
) {

    // 키: FallbackCacheKey, 값: 예외 타입 -> 메서드 맵
    private val fallbackMethodCache = ConcurrentHashMap<FallbackCacheKey, Map<Class<*>, Method>>()

    @Around("@annotation(circuitBreaker)")
    fun proceed(
        joinPoint: ProceedingJoinPoint,
        circuitBreaker: CircuitBreaker,
    ): Any? {
        val cb = registry.circuitBreaker(circuitBreaker.name)

        return try {
            cb.execute { joinPoint.proceed() }
        } catch (e: Exception) {
            executeFallback(joinPoint, circuitBreaker.fallbackMethod, e)
        }
    }

    private fun executeFallback(
        joinPoint: ProceedingJoinPoint,
        fallbackMethodName: String,
        originalException: Exception,
    ): Any? {
        if (fallbackMethodName.isBlank()) {
            throw originalException
        }

        val signature = joinPoint.signature as MethodSignature
        val cacheKey = FallbackCacheKey(
            fallbackMethodName,
            signature.method.parameterTypes.toList(),
            signature.method.returnType,
            joinPoint.target::class.java
        )

        val fallbackMap = getOrBuildFallbackMap(joinPoint.target::class.java, cacheKey)
        val fallbackMethod = findFallbackMethod(fallbackMap, originalException)
            ?: throw originalException

        return invokeFallback(fallbackMethod, joinPoint.args, originalException, joinPoint.target)
    }

    private fun getOrBuildFallbackMap(targetClass: Class<*>, cacheKey: FallbackCacheKey): Map<Class<*>, Method> {
        return fallbackMethodCache.computeIfAbsent(cacheKey) { key ->
            targetClass.declaredMethods // private 포함 모든 메서드 조회
                .filter { it.name == key.fallbackMethodName && it.parameterCount == key.params.size + 1 } // 이름과 파라미터 개수로 필터링
                .filter { Throwable::class.java.isAssignableFrom(it.parameterTypes.last()) } // 마지막 파라미터가 예외 타입인지 확인
                .associateBy { it.parameterTypes.last() } // 마지막 파라미터(예외 타입) 기준으로 매핑
                .mapValues { (_, method) -> method.apply { isAccessible = true } } // private 메서드 접근 허용
        }
    }


    private fun findFallbackMethod(fallbackMap: Map<Class<*>, Method>, exception: Exception): Method? {
        var exClass: Class<*>? = exception::class.java
        while (exClass != null && exClass != Any::class.java) {
            fallbackMap[exClass]?.let { return it }
            exClass = exClass.superclass // 상위 클래스 탐색
        }
        return null
    }

    private fun invokeFallback(
        method: Method,
        args: Array<Any>,
        exception: Exception,
        target: Any,
    ): Any? {
        val newArgs = args.plus(exception)
        return method.invoke(target, *newArgs)
    }

    private data class FallbackCacheKey(
        val fallbackMethodName: String,
        val params: List<Class<*>>,
        val returnType: Class<*>,
        val targetClass: Class<*>,
    )
}