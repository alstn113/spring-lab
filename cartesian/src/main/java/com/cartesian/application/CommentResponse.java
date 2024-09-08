package com.cartesian.ui;

import com.cartesian.domain.Comment;

public record CommentResponse(
        Long id,
        String content
) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent()
        );
    }
}
