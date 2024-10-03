package com.pagination.ui;

import java.util.List;
import com.pagination.application.post.PostResponse;
import com.pagination.application.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page // 페애지는 0부터 시작
    ) {
        List<PostResponse> response = postService.getPagingPosts(size, page);

        return ResponseEntity.ok(response);
    }

    // QueryDSL 사용
    @GetMapping("/posts/search")
    public ResponseEntity<PostResponse> getPost(
            @RequestParam(defaultValue = "title 1") String title
    ) {
        PostResponse response = postService.getPost(title);

        return ResponseEntity.ok(response);
    }

}
