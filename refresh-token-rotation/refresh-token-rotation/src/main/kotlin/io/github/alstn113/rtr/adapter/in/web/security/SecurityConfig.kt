package io.github.alstn113.rtr.adapter.`in`.web.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.alstn113.rtr.adapter.`in`.web.security.filter.AuthenticationFilter
import io.github.alstn113.rtr.adapter.`in`.web.security.filter.AuthorizationFilter
import io.github.alstn113.rtr.adapter.`in`.web.security.resolver.AuthenticationPrincipalArgumentResolver
import io.github.alstn113.rtr.application.port.out.TokenProvider
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val objectMapper: ObjectMapper,
    private val corsProperties: CorsProperties,
) : WebMvcConfigurer {

    /**
     * WebMvcConfigurer에 설정한 CORS는 요청이 DispatcherServlet에 도달한 후에 적용한다.
     * 그러므로, Filter 단계에서 CORS 요청이 차단될 수 있다. 이를 방지하기 위해 CorsFilter를 제일 앞에 등록한다.
     */
    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = corsProperties.allowedOrigins
        config.allowedMethods = corsProperties.allowedMethods
        config.allowedHeaders = corsProperties.allowedHeaders
        config.allowCredentials = corsProperties.allowCredentials
        config.maxAge = corsProperties.maxAge
        source.registerCorsConfiguration("/**", config)

        val registrationBean = FilterRegistrationBean<CorsFilter>()
        registrationBean.filter = CorsFilter(source)
        registrationBean.order = CORS_FILTER_ORDER
        registrationBean.addUrlPatterns("/*")
        return registrationBean
    }

    @Bean
    fun authenticationFilter(): FilterRegistrationBean<AuthenticationFilter> {
        val allowListPatterns = listOf(
            "/auth/sign-up",
            "/auth/login",
            "/auth/refresh"
        )

        val registrationBean = FilterRegistrationBean<AuthenticationFilter>()
        registrationBean.filter = AuthenticationFilter(tokenProvider, objectMapper, allowListPatterns)
        registrationBean.order = AUTHENTICATION_FILTER_ORDER
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
        registrationBean.order = AUTHORIZATION_FILTER_ORDER
        registrationBean.addUrlPatterns("/*")
        return registrationBean
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthenticationPrincipalArgumentResolver())
    }

    companion object {
        const val CORS_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE
        const val AUTHENTICATION_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 1
        const val AUTHORIZATION_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 2
    }
}