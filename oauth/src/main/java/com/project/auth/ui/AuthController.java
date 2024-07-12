package com.project.auth.ui;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class AuthController {

    private static final String REDIRECT_URI = "http://localhost:8080/auth/social/callback/github";
    private static final String CLIENT = "http://localhost:8080";

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final String clientId;
    private final String clientSecret;

    public AuthController(
            @Value("${auth.github.client-id}") String clientId,
            @Value("${auth.github.client-secret}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @GetMapping("/auth/social/redirect/github")
    public void githubRedirect(
            @RequestParam(value = "next", defaultValue = "/") String next,
            HttpServletResponse response
    ) throws IOException {
        String redirectUri = UriComponentsBuilder.fromHttpUrl(REDIRECT_URI)
                .queryParam("next", next)
                .build()
                .toUriString();

        log.info("Redirect URI: {}", redirectUri);

        String loginUrl = UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "user:email")
                .build()
                .toUriString();

        log.info("Login URL: {}", loginUrl);

        response.sendRedirect(loginUrl);
    }

    @GetMapping("/auth/social/callback/github")
    public void githubCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "next", defaultValue = "/") String next,
            HttpServletResponse response
    ) throws IOException {
        String accessToken = getAccessToken(code, clientId, clientSecret);
        log.info("Access token: {}", accessToken);

        SocialProfile socialProfile = getUserInfo(accessToken);
        log.info("Social profile: {}", socialProfile);

        String redirectUri = UriComponentsBuilder.fromHttpUrl(CLIENT)
                .path(next)
                .build()
                .toUriString();

        response.sendRedirect(redirectUri);
    }

    private String getAccessToken(String code, String clientId, String clientSecret) {
        RestClient restClient = RestClient.create();

        GithubAccessTokenRequest request = new GithubAccessTokenRequest(code, clientId, clientSecret);
        GithubAccessTokenResponse response = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GithubAccessTokenResponse.class);

        if (response == null) {
            throw new IllegalArgumentException("Failed to get access token");
        }

        return response.access_token();
    }

    private SocialProfile getUserInfo(String accessToken) {
        RestClient restClient = RestClient.create();

        SocialProfile socialProfile = restClient.get()
                .uri("https://api.github.com/user")
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(SocialProfile.class);

        if (socialProfile == null) {
            throw new IllegalArgumentException("Failed to get user info");
        }

        return socialProfile;
    }
}

