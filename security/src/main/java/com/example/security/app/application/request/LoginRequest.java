package com.example.security.app.application.request;

public record LoginRequest(
        String username,
        String password
) {
}
