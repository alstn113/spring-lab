package com.pagination.application.comment;

import java.util.List;
import com.pagination.application.reaction.ReactionMapper;
import com.pagination.domain.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ReactionMapper reactionMapper;

    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                reactionMapper.toResponses(comment.getReactions())
        );
    }

    public List<CommentResponse> toResponses(List<Comment> comments) {
        return comments.stream()
                .map(this::toResponse)
                .toList();
    }
}
