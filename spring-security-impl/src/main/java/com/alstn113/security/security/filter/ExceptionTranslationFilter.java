package com.alstn113.security.security.filter;

import com.alstn113.security.security.context.SecurityContext;
import com.alstn113.security.security.context.SecurityContextHolder;
import com.alstn113.security.security.exception.AccessDeniedHandler;
import com.alstn113.security.security.exception.AuthenticationEntryPoint;
import com.alstn113.security.security.exception.AuthenticationException;
import com.alstn113.security.security.exception.AuthorizationDeniedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.GenericFilterBean;

public class ExceptionTranslationFilter extends GenericFilterBean {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public ExceptionTranslationFilter(
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        } catch (AuthorizationDeniedException e) {
            if (isAnonymous()) {
                authenticationEntryPoint.commence(request, response, new AuthenticationException("인증되지 않은 사용자입니다."));
            } else {
                accessDeniedHandler.handle(request, response, e);
            }
        }
    }

    private boolean isAnonymous() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication() == null;
    }
}
