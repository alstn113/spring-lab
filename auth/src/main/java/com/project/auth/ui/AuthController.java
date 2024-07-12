package com.project.auth.ui;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class AuthController {



    private final Logger log = LoggerFactory.getLogger(AuthController.class);

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
                .queryParam("client_id", CLIENT_ID)
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
        String accessToken = getAccessToken(code, CLIENT_ID, CLIENT_SECRET);
        log.info("Access token: {}", accessToken);
        log.info("Next: {}", next);

        String redirectUri = UriComponentsBuilder.fromHttpUrl(CLIENT)
                .path(next)
                .build()
                .toUriString();

        response.sendRedirect(redirectUri);
    }

    private String getAccessToken(String code, String clientId, String clientSecret) {
        RestClient restClient = RestClient.builder().build();

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

    private void getUserInfo(String accessToken) {

    }
}

