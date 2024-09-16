package com.example.infra;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

    @MockBean
    private PostApiProperties postApiProperties;

    @Test
    @DisplayName("포스트 ID로 포스트를 가져온다.")
    void getPostById() throws JsonProcessingException {
        PostApiResponse expectedResponse = new PostApiResponse(1L, 1L, "title", "body");
        given(postApiProperties.url()).willReturn("https://jsonplaceholder.typicode.com/posts");

        mockServer.expect(requestTo("https://jsonplaceholder.typicode.com/posts/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(expectedResponse)));

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
