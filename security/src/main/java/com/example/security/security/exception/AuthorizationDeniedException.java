package com.example.security.security.exception;

import java.nio.file.AccessDeniedException;

public class AuthorizationDeniedException extends AccessDeniedException {

    public AuthorizationDeniedException(String message) {
        super(message);
    }
}
