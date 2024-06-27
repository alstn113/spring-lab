package com.project.requestscope.advanced.infra.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BearerAuthorizationExtractor implements AuthorizationExtractor<String> {

    @Override
    public Optional<String> extract(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authorization.substring(7));
    }
}
