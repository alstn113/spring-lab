package com.example.security.security.filter;

import com.example.security.security.authorization.AuthorityAuthorizationManager;
import com.example.security.security.authorization.AuthorizationDecision;
import com.example.security.security.authorization.AuthorizationManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpMethod;

public class AuthorizeHttpRequests {

    private final List<RequestMatcherEntry> mappings = new ArrayList<>();

    public MatcherConfig requestMatchers(String... patterns) {
        return new MatcherConfig(patterns);
    }

    public MatcherConfig requestMatchers(HttpMethod method, String... patterns) {
        return new MatcherConfig(method, patterns);
    }

    public MatcherConfig anyRequest() {
        return new MatcherConfig("/**");
    }

    public List<RequestMatcherEntry> build() {
        return mappings;
    }

    public class MatcherConfig {

        private static final AuthorizationManager permitAllAuthorizationManager =
                authentication -> new AuthorizationDecision(true);
        private static final AuthorizationManager authenticatedAuthorizationManager =
                authentication -> new AuthorizationDecision(authentication.get() != null);

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
            List<RequestMatcher> matchers = createMatchers();
            return access(matchers, permitAllAuthorizationManager);
        }

        public AuthorizeHttpRequests authenticated() {
            List<RequestMatcher> matchers = createMatchers();
            return access(matchers, authenticatedAuthorizationManager);
        }

        public AuthorizeHttpRequests hasAuthority(String authority) {
            List<RequestMatcher> matchers = createMatchers();
            return access(matchers, AuthorityAuthorizationManager.hasAuthority(authority));
        }

        private List<RequestMatcher> createMatchers() {
            List<RequestMatcher> matchers = new ArrayList<>();
            for (String pattern : patterns) {
                matchers.add(new RequestMatcher(method, pattern));
            }
            return matchers;
        }

        private AuthorizeHttpRequests access(List<? extends RequestMatcher> matchers, AuthorizationManager manager) {
            for (RequestMatcher matcher : matchers) {
                mappings.add(new RequestMatcherEntry(matcher, manager));
            }

            return AuthorizeHttpRequests.this;
        }
    }

    public record RequestMatcherEntry(RequestMatcher matcher, AuthorizationManager manager) {
    }
}
