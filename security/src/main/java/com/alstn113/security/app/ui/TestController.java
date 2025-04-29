package com.alstn113.security.app.ui;

import com.alstn113.security.security.annotation.AuthenticationPrincipal;
import com.alstn113.security.security.authentication.JwtAuthentication;
import com.alstn113.security.security.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public")
    public String test(@AuthenticationPrincipal JwtAuthentication authentication) {
        if (authentication == null) {
            return "Public endpoint.";
        }
        return "Hello #" + authentication.principal();
    }

    @PostMapping("/public")
    public String testPost(@AuthenticationPrincipal JwtAuthentication authentication) {
        return "Hello #" + authentication.principal();
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
    public String privateAdmin(@AuthenticationPrincipal JwtAuthentication authentication) {
        return "Admin #" + authentication.principal();
    }
}
