package com.example.security.security.filter;

import com.example.security.security.authorization.AuthorizationFilter;
import com.example.security.security.exception.AccessDeniedHandler;
import com.example.security.security.exception.AuthenticationEntryPoint;
import com.example.security.security.filter.HttpSecurity.AuthorizeHttpRequests.AccessRule;
import jakarta.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class HttpSecurity {

    private final List<Filter> preAuthenticationFilters = new ArrayList<>();
    private final List<AccessRule> accessRules = new ArrayList<>();

    private AuthenticationEntryPoint entryPoint;
    private AccessDeniedHandler deniedHandler;

    public HttpSecurity(AuthenticationEntryPoint entryPoint, AccessDeniedHandler deniedHandler) {
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    public HttpSecurity addFilterBefore(Filter filter) {
        preAuthenticationFilters.add(filter);
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
        AuthorizeHttpRequests authConfig = new AuthorizeHttpRequests();
        configurer.accept(authConfig);
        accessRules.addAll(authConfig.build());
        return this;
    }


    public Filter build() {
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(entryPoint, deniedHandler);
        ExceptionTranslationFilter exceptionFilter = new ExceptionTranslationFilter(entryPoint, deniedHandler);

        List<SecurityFilterChain> filterChains = accessRules.stream()
                .map(rule -> {
                    List<Filter> filters = new ArrayList<>(preAuthenticationFilters);

                    // 모든 요청에 대해서 authorizationFilter 를 추가하고, HttpSecurity 에서 AuthorizationManager 를 구현한 것들을 추가
                    if (!rule.permit()) {
                        filters.add(authorizationFilter);
                    }
                    filters.add(exceptionFilter);

                    return new SecurityFilterChain(rule.matcher(), filters);
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

    public static class AuthorizeHttpRequests {
        private final List<AccessRule> rules = new ArrayList<>();

        public MatcherConfig requestMatchers(String... patterns) {
            return new MatcherConfig(patterns);
        }

        public MatcherConfig requestMatchers(HttpMethod method, String... patterns) {
            return new MatcherConfig(method, patterns);
        }

        public MatcherConfig anyRequest() {
            return new MatcherConfig("/**");
        }

        public List<AccessRule> build() {
            return rules;
        }

        public class MatcherConfig {
            private final HttpMethod method;
            private final String[] patterns;

            public MatcherConfig(String... patterns) {
                this(null, patterns);
            }

            public MatcherConfig(HttpMethod method, String... patterns) {
                this.method = method;
                this.patterns = patterns;
            }

            public AuthorizeHttpRequests permitAll() {
                for (String pattern : patterns) {
                    rules.add(new AccessRule(new RequestMatcher(method, pattern), true));
                }
                return AuthorizeHttpRequests.this;
            }

            public AuthorizeHttpRequests authenticated() {
                for (String pattern : patterns) {
                    rules.add(new AccessRule(new RequestMatcher(method, pattern), false));
                }
                return AuthorizeHttpRequests.this;
            }
        }

        public record AccessRule(RequestMatcher matcher, boolean permit) {
        }
    }
}