package com.cartesian.ui;

import java.util.List;
import com.cartesian.domain.Comment;
import com.cartesian.domain.Post;

public record PostResponse(
        Long id,
        String title,
        List<CommentResponse> comments
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                CommentResponse.from(post.comments())
        );
    }
}
