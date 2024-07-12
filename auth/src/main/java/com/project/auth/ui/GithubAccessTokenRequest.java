package com.project.auth.ui;

public record GithubAccessTokenRequest(String code, String client_id, String client_secret) {
}
