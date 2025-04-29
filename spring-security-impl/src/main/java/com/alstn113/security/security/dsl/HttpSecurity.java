package com.alstn113.security.security.dsl;

import com.alstn113.security.security.exception.AccessDeniedHandler;
import com.alstn113.security.security.exception.AuthenticationEntryPoint;
import com.alstn113.security.security.filter.RequestMatcher;
import com.alstn113.security.security.filter.SecurityFilterChain;
import jakarta.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class HttpSecurity {

    private final List<Filter> filters;
    private final ExceptionHandlingConfigurer exceptionHandlingConfigurer;
    private final AuthorizeHttpRequestsConfigurer authorizeHttpRequestsConfigurer;

    public HttpSecurity(AuthenticationEntryPoint entryPoint, AccessDeniedHandler deniedHandler) {
        this.filters = new ArrayList<>();
        this.exceptionHandlingConfigurer = new ExceptionHandlingConfigurer(entryPoint, deniedHandler);
        this.authorizeHttpRequestsConfigurer = new AuthorizeHttpRequestsConfigurer();
    }

    public HttpSecurity addFilterBefore(Filter filter) {
        filters.addFirst(filter);
        return this;
    }

    public HttpSecurity addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public HttpSecurity exceptionHandling(Consumer<ExceptionHandlingConfigurer> consumer) {
        consumer.accept(exceptionHandlingConfigurer);
        return this;
    }

    public HttpSecurity authorizeHttpRequests(
            Consumer<AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry> consumer
    ) {
        consumer.accept(authorizeHttpRequestsConfigurer.getRegistry());
        return this;
    }

    public SecurityFilterChain build() {
        exceptionHandlingConfigurer.configure(this);
        authorizeHttpRequestsConfigurer.configure(this);

        return new SecurityFilterChain(RequestMatcher.ANY_REQUEST, filters);
    }
}
