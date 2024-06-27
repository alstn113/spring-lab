package com.project.requestscope.advanced.infra.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    private final AuthorizationExtractor<String> authorizationExtractor;
    private final AuthContext authContext;

    public AuthInterceptor(AuthorizationExtractor<String> authorizationExtractor, AuthContext authContext) {
        this.authorizationExtractor = authorizationExtractor;
        this.authContext = authContext;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String token = authorizationExtractor.extract(request)
                .orElseThrow(() -> new IllegalStateException("No token found"));

        authContext.setAuthIfAbsent(new Auth(Long.parseLong(token)));

        log.info("AuthInterceptor.preHandle: authContext.getAuth()={}", authContext.getAuth());

        return true;
    }
}
