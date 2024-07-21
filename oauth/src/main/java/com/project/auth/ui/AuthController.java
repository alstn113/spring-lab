package com.project.auth.ui;

import java.io.IOException;
import com.project.auth.application.AuthService;
import com.project.auth.application.MemberResponse;
import com.project.auth.application.MemberService;
import com.project.auth.domain.Provider;
import com.project.auth.infra.oauth.GithubOAuthService;
import com.project.auth.infra.oauth.SocialProfile;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final String TOKEN_COOKIE_NAME = "token";
    private static final int COOKIE_MAX_AGE_ONE_DAY = 60 * 60 * 24;

    private final GithubOAuthService githubOAuthService;
    private final MemberService memberService;
    private final AuthService authService;

    public AuthController(GithubOAuthService githubOAuthService, MemberService memberService, AuthService authService) {
        this.githubOAuthService = githubOAuthService;
        this.memberService = memberService;
        this.authService = authService;
    }

    @GetMapping("/auth/social/redirect/github")
    public void githubRedirect(
            @RequestParam(value = "next", defaultValue = "/") String next,
            HttpServletResponse response
    ) throws IOException {
        String redirectUri = githubOAuthService.getLoginUrl(next);
        response.sendRedirect(redirectUri);
    }

    @GetMapping("/auth/social/callback/github")
    public void githubCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "next", defaultValue = "/") String next,
            HttpServletResponse response
    ) throws IOException {
        String accessToken = githubOAuthService.getAccessToken(code);
        SocialProfile socialProfile = githubOAuthService.getUserInfo(accessToken);

        MemberResponse memberResponse = memberService.findOrCreateMember(socialProfile, Provider.GITHUB);
        String token = authService.createToken(memberResponse.id());

        setTokenCookie(response, token);

        // Token을 쿠키에 저장하는게 아닌 query param으로 전달할 수도 있습니다.
        // ex) http://localhost:8080/?token=abc
        String redirectUri = githubOAuthService.getClientUri(next);
        response.sendRedirect(redirectUri);
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearTokenCookie(response);

        return ResponseEntity.noContent().build();
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setMaxAge(COOKIE_MAX_AGE_ONE_DAY);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    private void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
