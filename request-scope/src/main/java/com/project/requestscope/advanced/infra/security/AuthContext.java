package com.project.requestscope.advanced.infra.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthContext {

    private Auth auth;

    public boolean hasAuth() {
        return auth != null;
    }

    public Auth getAuth() {
        if (auth == null) {
            throw new IllegalStateException("Auth is not bound");
        }

        return auth;
    }

    public void setAuthIfAbsent(Auth auth) {
        if (this.auth != null) {
            throw new IllegalStateException("Auth is already bound");
        }

        this.auth = auth;
    }
}
