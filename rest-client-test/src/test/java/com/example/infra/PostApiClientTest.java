package com.example.infra;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(PostApiClient.class)
class PostApiClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostApiClient postApiClient;

    @Test
    @DisplayName("포스트 ID로 포스트를 가져온다.")
    void getPostById() throws JsonProcessingException {
        String expectedResponse = objectMapper.writeValueAsString(new PostApiResponse(1L, 1L, "title", "body"));

        mockServer.expect(requestTo("https://jsonplaceholder.typicode.com/posts/1"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        PostApiResponse postApiResponse = postApiClient.getPostById(1L);

        assertSoftly(softly -> {
            softly.assertThat(postApiResponse).isNotNull();
            softly.assertThat(postApiResponse.id()).isEqualTo(1);
            softly.assertThat(postApiResponse.userId()).isEqualTo(1);
            softly.assertThat(postApiResponse.title()).isEqualTo("title");
            softly.assertThat(postApiResponse.body()).isEqualTo("body");
        });
    }
}
