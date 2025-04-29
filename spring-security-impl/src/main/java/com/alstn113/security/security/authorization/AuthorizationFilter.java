package com.alstn113.security.security.authorization;

import com.alstn113.security.security.context.Authentication;
import com.alstn113.security.security.context.SecurityContextHolder;
import com.alstn113.security.security.exception.AuthorizationDeniedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@Getter
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationManager authorizationManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationResult result = authorizationManager.authorize(() -> authentication, request);

        if (result != null && !result.isGranted()) {
            throw new AuthorizationDeniedException("Access Denied");
        }

        filterChain.doFilter(request, response);
    }
}
