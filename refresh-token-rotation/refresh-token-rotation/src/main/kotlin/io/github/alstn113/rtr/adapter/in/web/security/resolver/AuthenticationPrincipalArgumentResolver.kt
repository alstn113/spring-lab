package io.github.alstn113.rtr.adapter.`in`.web.security.resolver

import io.github.alstn113.rtr.adapter.`in`.web.security.AccountPrincipal
import io.github.alstn113.rtr.adapter.`in`.web.security.AttributeNames
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AuthenticationPrincipalArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal::class.java) &&
                parameter.parameterType == AccountPrincipal::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): AccountPrincipal {
        val request = webRequest.nativeRequest as HttpServletRequest
        return request.getAttribute(AttributeNames.AUTHENTICATION_PRINCIPAL) as AccountPrincipal?
            ?: AccountPrincipal.ANONYMOUS
    }
}