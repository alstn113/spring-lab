package com.example.security.security.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityContext {

    private Authentication authentication;

    public SecurityContext(Authentication authentication) {
        this.authentication = authentication;
    }

    public static SecurityContext createEmptyContext() {
        return new SecurityContext(null);
    }
}
