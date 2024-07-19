package com.project.auth.application;

public interface TokenProvider {

    String createToken(String memberId);

    Long getMemberId(String token);
}
