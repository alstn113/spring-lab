package com.example.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.infra.PostApiClient;
import com.example.infra.PostApiResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
class PostFacadeServiceTest {

    @MockBean
    private PostApiClient postApiClient;

    @Autowired
    private PostFacadeService postFacadeService;

    @Test
    @DisplayName("포스트 ID로 포스트를 가져온다.")
    void getPostById() {
        // given
        Long postId = 1L;
        PostApiResponse apiResponse = new PostApiResponse(1L, postId, "title", "body");
        given(postApiClient.getPostById(any())).willReturn(apiResponse);

        // when
        CreatePostRequest request = new CreatePostRequest(postId);
        PostResponse result = postFacadeService.createPostFromExternalApi(request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.title()).isEqualTo("title");
            softly.assertThat(result.body()).isEqualTo("body");
        });
    }
}
