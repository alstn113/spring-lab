package com.example.application;

import com.example.infra.PostApiClient;
import com.example.infra.PostApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostFacadeService {

    private final PostService postService;
    private final PostApiClient postApiClient;

    public PostResponse createPostFromExternalApi(CreatePostRequest request) {
        PostApiResponse response = postApiClient.getPostById(request.postId());

        return postService.createPost(response.title(), response.body());
    }
}
