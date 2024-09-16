package com.example.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PostApiClient {

    private static final Logger log = LoggerFactory.getLogger(PostApiClient.class);

    private final PostApiProperties properties;
    private final RestClient restClient;

    public PostApiClient(
            PostApiProperties properties,
            RestClient.Builder restClientBuilder
    ) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    public PostApiResponse getPostById(Long id) {
        log.info("Fetching post by id: {}", id);

        String url = "%s/%d".formatted(properties.url(), id);
        PostApiResponse postApiResponse = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalArgumentException("Post API 4xx Failed");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new IllegalArgumentException("Post API 5xx Failed");
                })
                .body(PostApiResponse.class);

        log.info("Post API response: {}", postApiResponse);

        return postApiResponse;
    }
}
