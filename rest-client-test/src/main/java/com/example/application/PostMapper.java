package com.example.application;

import com.example.domain.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponse toResponse(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getBody());
    }
}
