package com.example.security.security.authentication;

import com.example.security.security.context.Authentication;

public class JwtAuthentication implements Authentication {

    private final Long memberId;

    public JwtAuthentication(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public Long principal() {
        return memberId;
    }

    @Override
    public String toString() {
        return "JwtAuthentication{" +
                "memberId=" + memberId +
                '}';
    }
}
