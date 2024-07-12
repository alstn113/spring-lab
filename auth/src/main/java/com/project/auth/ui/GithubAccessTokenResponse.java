package com.project.auth.ui;

public record GithubAccessTokenResponse(String access_token, String token_type, String scope) {
}
