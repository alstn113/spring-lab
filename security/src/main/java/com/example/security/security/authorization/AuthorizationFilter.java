package com.example.security.security.authorization;

import com.example.security.security.context.Authentication;
import com.example.security.security.context.SecurityContextHolder;
import com.example.security.security.exception.AccessDeniedHandler;
import com.example.security.security.exception.AuthenticationEntryPoint;
import com.example.security.security.exception.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Getter
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("AuthorizationFilter doFilterInternal");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("AuthorizationFilter doFilterInternal: 인증 정보 없음");
            authenticationEntryPoint.commence(request, response, new AuthenticationException("로그인이 필요합니다."));
            return;
        }

        if (!hasAccess(authentication, request)) {
            log.debug("AuthorizationFilter doFilterInternal: 접근 권한 없음");
            accessDeniedHandler.handle(request, response, new AccessDeniedException("접근할 권한이 없습니다."));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasAccess(Authentication authentication, HttpServletRequest request) {
        return true;
    }
}
