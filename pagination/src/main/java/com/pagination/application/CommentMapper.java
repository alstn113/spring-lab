package com.pagination.application;

import java.util.List;
import com.pagination.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent());
    }

    public List<CommentResponse> toResponses(List<Comment> comments) {
        return comments.stream()
                .map(this::toResponse)
                .toList();
    }
}
