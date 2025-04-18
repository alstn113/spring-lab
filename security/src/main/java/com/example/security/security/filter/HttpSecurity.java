package com.example.security.security.filter;

import com.example.security.security.authorization.AuthorizationFilter;
import com.example.security.security.exception.AccessDeniedHandler;
import com.example.security.security.exception.AuthenticationEntryPoint;
import jakarta.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class HttpSecurity {

    private final List<Filter> authenticationFilters = new ArrayList<>();
    private final AuthorizeHttpRequests authorizeHttpRequests = new AuthorizeHttpRequests();

    private AuthenticationEntryPoint entryPoint;
    private AccessDeniedHandler deniedHandler;

    public HttpSecurity(AuthenticationEntryPoint entryPoint, AccessDeniedHandler deniedHandler) {
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    public HttpSecurity addFilterBefore(Filter filter) {
        authenticationFilters.add(filter);
        return this;
    }

    public HttpSecurity exceptionHandling(Consumer<ExceptionHandlingConfigurer> configurer) {
        ExceptionHandlingConfigurer config = new ExceptionHandlingConfigurer(entryPoint, deniedHandler);
        configurer.accept(config);

        if (config.entryPoint != null) {
            this.entryPoint = config.entryPoint;
        }
        if (config.deniedHandler != null) {
            this.deniedHandler = config.deniedHandler;
        }

        return this;
    }

    public HttpSecurity authorizeHttpRequests(Consumer<AuthorizeHttpRequests> configurer) {
        configurer.accept(authorizeHttpRequests);
        return this;
    }


    public Filter build() {
        ExceptionTranslationFilter exceptionFilter = new ExceptionTranslationFilter(entryPoint, deniedHandler);

        List<SecurityFilterChain> filterChains = authorizeHttpRequests.build().stream()
                .map(it -> {
                    List<Filter> filters = new ArrayList<>(authenticationFilters);
                    filters.add(exceptionFilter);
                    filters.add(new AuthorizationFilter(it.manager(), entryPoint, deniedHandler));

                    return new SecurityFilterChain(it.matcher(), filters);
                })
                .toList();

        return new FilterChainProxy(filterChains);
    }

    public static class ExceptionHandlingConfigurer {
        private AuthenticationEntryPoint entryPoint;
        private AccessDeniedHandler deniedHandler;

        public ExceptionHandlingConfigurer(AuthenticationEntryPoint entryPoint, AccessDeniedHandler deniedHandler) {
            this.entryPoint = entryPoint;
            this.deniedHandler = deniedHandler;
        }

        public ExceptionHandlingConfigurer authenticationEntryPoint(AuthenticationEntryPoint entryPoint) {
            this.entryPoint = entryPoint;
            return this;
        }

        public ExceptionHandlingConfigurer accessDeniedHandler(AccessDeniedHandler deniedHandler) {
            this.deniedHandler = deniedHandler;
            return this;
        }
    }
}