package com.project.requestscope.advanced.infra.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.web.context.request.NativeWebRequest;

public interface AuthorizationExtractor<T> {

    String AUTHORIZATION = "Authorization";

    Optional<T> extract(HttpServletRequest request);

    default Optional<T> extract(NativeWebRequest request) {
        HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);

        if (httpServletRequest == null) {
            return Optional.empty();
        }

        return extract(httpServletRequest);
    }
}
