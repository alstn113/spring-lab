package com.example.security.app.ui;

import com.example.security.security.annotation.AuthenticationPrincipal;
import com.example.security.security.authentication.JwtAuthentication;
import com.example.security.security.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public")
    public String test() {
        return "Test endpoint is working!";
    }

    @GetMapping("/private")
    public String publicEndpoint() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "Unauthorized access!";
        }
        return "Hello! you are %d".formatted(authentication.principal());
    }

    @GetMapping("/private/2")
    public String privateEndpoint2(@AuthenticationPrincipal JwtAuthentication authentication) {
        if (authentication == null) {
            return "Unauthorized access!";
        }
        return "Hello! you are %d".formatted(authentication.principal());
    }
}
