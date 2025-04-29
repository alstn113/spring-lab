package com.alstn113.security.security.dsl;

import com.alstn113.security.security.authorization.AuthorizationManager;
import com.alstn113.security.security.filter.RequestMatcher;

public record RequestMatcherEntry(RequestMatcher matcher, AuthorizationManager manager) {
}
