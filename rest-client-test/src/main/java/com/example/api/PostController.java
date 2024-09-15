package com.example.api;

import java.net.URI;
import com.example.application.CreatePostRequest;
import com.example.application.PostFacadeService;
import com.example.application.PostResponse;
import com.example.application.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostFacadeService postFacadeService;

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> getPosts(@RequestBody CreatePostRequest request) {
        PostResponse response = postFacadeService.createPostFromExternalApi(request);

        URI location = URI.create("/posts/%d".formatted(response.id()));
        return ResponseEntity.created(location).body(response);
    }
}
