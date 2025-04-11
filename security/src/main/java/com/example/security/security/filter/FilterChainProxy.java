package com.example.security.security.filter;

import com.example.security.security.authorization.AuthorizationFilter;
import com.example.security.security.exception.AuthenticationException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class FilterChainProxy extends GenericFilterBean {

    private final AuthorizationFilter authorizationFilter;
    private final List<SecurityFilterChain> filterChains;

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            processFilter(request, response, chain);
        } catch (AuthenticationException e) {
            authorizationFilter.getAuthenticationEntryPoint().commence(request, response, e);
        } catch (AccessDeniedException e) {
            authorizationFilter.getAccessDeniedHandler().handle(request, response, e);
        }
    }

    private void processFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        for (SecurityFilterChain filterChain : filterChains) {
            if (filterChain.matches(request)) {
                List<Filter> filters = filterChain.getFilters();
                VirtualFilterChain virtualFilterChain = new VirtualFilterChain(filters, chain);
                virtualFilterChain.doFilter(request, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
