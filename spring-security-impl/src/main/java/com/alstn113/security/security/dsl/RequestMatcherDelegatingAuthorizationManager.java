package com.alstn113.security.security.dsl;

import com.alstn113.security.security.authorization.AuthorizationDecision;
import com.alstn113.security.security.authorization.AuthorizationManager;
import com.alstn113.security.security.context.Authentication;
import com.alstn113.security.security.filter.RequestMatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RequestMatcherDelegatingAuthorizationManager implements AuthorizationManager {

    private final List<RequestMatcherEntry> mappings;

    public RequestMatcherDelegatingAuthorizationManager() {
        this.mappings = new ArrayList<>();
    }

    @Override
    public AuthorizationDecision authorize(Supplier<Authentication> authentication, HttpServletRequest request) {
        for (RequestMatcherEntry entry : mappings) {
            RequestMatcher matcher = entry.matcher();
            AuthorizationManager manager = entry.manager();

            if (matcher.matches(request)) {
                return manager.authorize(authentication, request);
            }
        }
        return new AuthorizationDecision(false);
    }

    public RequestMatcherDelegatingAuthorizationManager add(RequestMatcher matcher, AuthorizationManager manager) {
        mappings.add(new RequestMatcherEntry(matcher, manager));
        return this;
    }
}
