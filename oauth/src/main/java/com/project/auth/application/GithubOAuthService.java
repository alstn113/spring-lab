package com.project.auth.application;

import com.project.auth.application.oauth.GithubAccessTokenRequest;
import com.project.auth.application.oauth.GithubAccessTokenResponse;
import com.project.auth.application.oauth.GithubOAuthClient;
import com.project.auth.application.oauth.GithubOAuthProperties;
import com.project.auth.application.oauth.SocialProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GithubOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GithubOAuthService.class);

    private final GithubOAuthClient githubOAuthClient;
    private final GithubOAuthProperties properties;

    public GithubOAuthService(
            GithubOAuthClient githubOAuthClient,
            GithubOAuthProperties properties
    ) {
        this.githubOAuthClient = githubOAuthClient;
        this.properties = properties;
    }

    public String getLoginUrl(String next) {
        String redirectUriWithNext = UriComponentsBuilder.fromHttpUrl(properties.redirectUri())
                .queryParam("next", next)
                .build()
                .toUriString();

        return UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/authorize")
                .queryParam("client_id", properties.clientId())
                .queryParam("redirect_uri", redirectUriWithNext)
                .queryParam("scope", "user:email")
                .build()
                .toUriString();
    }

    public String getAccessToken(String code) {
        GithubAccessTokenRequest request = new GithubAccessTokenRequest(
                code,
                properties.clientId(),
                properties.clientSecret()
        );
        GithubAccessTokenResponse response = githubOAuthClient.getAccessToken(request);

        if (response == null) {
            throw new IllegalArgumentException("액세스 토큰을 가져오는데 실패했습니다.");
        }

        log.info("✅ access token: {}", response.accessToken());

        return response.accessToken();
    }

    public SocialProfile getUserInfo(String accessToken) {
        SocialProfile socialProfile = githubOAuthClient.getUserInfo(accessToken);

        if (socialProfile == null) {
            throw new IllegalArgumentException("사용자 정보를 가져오는데 실패했습니다.");
        }

        log.info("✅ social profile: {}", socialProfile);

        return socialProfile;
    }

    public String getClientUri(String next) {
        return UriComponentsBuilder.fromHttpUrl(properties.clientUri())
                .path(next)
                .build()
                .toUriString();
    }
}
