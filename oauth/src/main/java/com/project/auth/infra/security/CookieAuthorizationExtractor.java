package com.project.auth.infra.security;

import java.util.Arrays;
import java.util.Optional;
import com.project.auth.application.AuthorizationExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CookieAuthorizationExtractor implements AuthorizationExtractor<String> {

    private static final String TOKEN_COOKIE_NAME = "token";

    @Override
    public Optional<String> extract(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
