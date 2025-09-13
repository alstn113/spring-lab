package io.github.alstn113.rtr.adapter.`in`.web.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.alstn113.rtr.adapter.`in`.web.security.filter.AuthenticationFilter
import io.github.alstn113.rtr.adapter.`in`.web.security.filter.AuthorizationFilter
import io.github.alstn113.rtr.adapter.`in`.web.security.resolver.AuthenticationPrincipalArgumentResolver
import io.github.alstn113.rtr.application.port.out.TokenProvider
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val objectMapper: ObjectMapper,
    private val corsProperties: CorsProperties,
) : WebMvcConfigurer {

    @Bean
    fun authenticationFilter(): FilterRegistrationBean<AuthenticationFilter> {
        val allowListPatterns = listOf(
            "/auth/sign-up",
            "/auth/login",
            "/auth/refresh"
        )

        val registrationBean = FilterRegistrationBean<AuthenticationFilter>()
        registrationBean.filter = AuthenticationFilter(tokenProvider, objectMapper, allowListPatterns)
        registrationBean.order = 1
        registrationBean.addUrlPatterns("/*")
        return registrationBean
    }

    @Bean
    fun authorizationFilter(): FilterRegistrationBean<AuthorizationFilter> {
        val allowListPatterns = listOf(
            "/auth/sign-up",
            "/auth/login",
            "/auth/refresh",
            "/test" // 인증된 사용자와 인증되지 않은 사용자 모두 접근 가능,
        )

        val registrationBean = FilterRegistrationBean<AuthorizationFilter>()
        registrationBean.filter = AuthorizationFilter(objectMapper, allowListPatterns)
        registrationBean.order = 2
        registrationBean.addUrlPatterns("/*")
        return registrationBean
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthenticationPrincipalArgumentResolver())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*corsProperties.allowedOrigins.toTypedArray())
            .allowedMethods(*corsProperties.allowedMethods.toTypedArray())
            .allowedHeaders(*corsProperties.allowedHeaders.toTypedArray())
            .allowCredentials(corsProperties.allowCredentials)
            .maxAge(corsProperties.maxAge)
    }
}