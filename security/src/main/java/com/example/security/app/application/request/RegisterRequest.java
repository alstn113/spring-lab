package com.example.security.app.application.request;

public record RegisterRequest(
        String email,
        String password
) {
}
