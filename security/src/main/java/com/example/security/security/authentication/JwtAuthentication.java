package com.example.security.security.authentication;

import com.example.security.security.authorization.GrantedAuthority;
import com.example.security.security.context.Authentication;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthentication implements Authentication {

    private final Long memberId;
    private final Collection<GrantedAuthority> authorities;

    public JwtAuthentication(Long memberId, Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.authorities = new ArrayList<>(authorities);
    }

    @Override
    public Long principal() {
        return memberId;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "JwtAuthentication{" +
                "memberId=" + memberId +
                '}';
    }
}
