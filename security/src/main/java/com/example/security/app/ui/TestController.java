package com.example.security.app.ui;

import com.example.security.security.annotation.AuthenticationPrincipal;
import com.example.security.security.authentication.JwtAuthentication;
import com.example.security.security.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public")
    public String test() {
        return "Public endpoint.";
    }

    @PostMapping("/public")
    public String testPost(@AuthenticationPrincipal JwtAuthentication authentication) {
        return "Member #" + authentication.principal();
    }

    @GetMapping("/private/member")
    public String privateMember(@AuthenticationPrincipal JwtAuthentication authentication) {
        return "Member #" + authentication.principal();
    }

    @GetMapping("/private/member/holder")
    public String privateMemberHolder() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return "Member #" + authentication.principal();
    }

    @GetMapping("/private/admin")
    public String adminEndpoint(@AuthenticationPrincipal JwtAuthentication authentication) {
        return "Admin #" + authentication.principal();
    }
}
