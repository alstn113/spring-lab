package com.example.security.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SecurityFilterChain {

    private final RequestMatcher requestMatcher;
    private final List<Filter> filters;

    public boolean matches(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    public List<Filter> getFilters() {
        return new ArrayList<>(filters);
    }
}