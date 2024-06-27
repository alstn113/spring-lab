package com.project.requestscope.advanced.infra.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final Logger log = LoggerFactory.getLogger(AuthArgumentResolver.class);
    private final AuthorizationExtractor<String> authorizationExtractor;
    private final AuthContext authContext;

    public AuthArgumentResolver(AuthorizationExtractor<String> authorizationExtractor, AuthContext authContext) {
        this.authorizationExtractor = authorizationExtractor;
        this.authContext = authContext;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.hasParameterAnnotation(BindAuth.class);
        boolean isAuthType = Auth.class.isAssignableFrom(parameter.getParameterType());

        return hasAuthAnnotation && isAuthType;
    }

    @Override
    public Auth resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        if (authContext.hasAuth()) {
            log.info("AuthArgumentResolver.resolveArgument: authContext.getAuth()={}", authContext.getAuth());
            return authContext.getAuth();
        }

        String token = authorizationExtractor.extract(webRequest)
                .orElseThrow(() -> new IllegalStateException("No token found"));

        // 나머지 과정은 생략

        log.info("AuthArgumentResolver.resolveArgument: token={}", token);

        return new Auth(Long.parseLong(token));
    }
}
