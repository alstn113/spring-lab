package com.example.security.security.authorization;

import java.util.Collection;

public class AuthorityAuthorizationDecision extends AuthorizationDecision {

    private final Collection<GrantedAuthority> authorities;

    public AuthorityAuthorizationDecision(boolean granted, Collection<GrantedAuthority> authorities) {
        super(granted);
        this.authorities = authorities;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
