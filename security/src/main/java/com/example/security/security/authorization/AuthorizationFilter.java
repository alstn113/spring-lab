package com.example.security.security.authorization;

import com.example.security.security.context.Authentication;
import com.example.security.security.context.SecurityContextHolder;
import com.example.security.security.exception.AuthorizationDeniedException;
import com.example.security.security.exception.AccessDeniedHandler;
import com.example.security.security.exception.AuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Getter
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationManager authorizationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationResult result = authorizationManager.authorize(() -> authentication);

        if (result != null && !result.isGranted()) {
            throw new AuthorizationDeniedException("Access Denied");
        }

        filterChain.doFilter(request, response);
    }
}
