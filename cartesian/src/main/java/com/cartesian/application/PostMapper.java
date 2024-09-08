package com.cartesian.ui;

import com.cartesian.domain.Post;

public class PostMapper {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                CommentResponse.from(post.comments())
        );
    }
}
