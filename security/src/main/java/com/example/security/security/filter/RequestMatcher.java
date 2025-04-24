package com.example.security.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

public class RequestMatcher {
    private final String pattern;
    private final HttpMethod method;

    public RequestMatcher(HttpMethod method, String pattern) {
        this.method = method;
        this.pattern = pattern;
    }

    public boolean matches(HttpServletRequest request) {
        boolean pathMatches = pathMatches(request.getRequestURI());

        if (method == null) {
            return pathMatches;
        }

        return pathMatches && method.matches(request.getMethod());
    }

    private boolean pathMatches(String path) {
        if (pattern.endsWith("/**")) {
            String base = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(base);
        }
        return path.equals(pattern);
    }
}
