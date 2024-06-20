package com.project.event;

import com.project.event.application.PostService;
import com.project.event.domain.Post;
import com.project.event.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("일반적인 이벤트 테스트")
    void normalEventTest() {
        Post savedPost = postService.createPost(new Post("title", "https://image.url"));

        System.out.println(postRepository.findById(savedPost.getId()).get());

        postService.deletePostById(savedPost.getId());
    }
}
