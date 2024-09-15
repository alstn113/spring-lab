package com.example.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PostApiClient {

    private static final Logger log = LoggerFactory.getLogger(PostApiClient.class);

    private final RestClient restClient;

    public PostApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public PostApiResponse getPostById(Long id) {
        log.info("Fetching post by id: {}", id);

        PostApiResponse postApiResponse = restClient.get()
                .uri("https://jsonplaceholder.typicode.com/posts/%d".formatted(id))
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
