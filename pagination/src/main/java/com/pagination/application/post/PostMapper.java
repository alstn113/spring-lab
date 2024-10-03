package com.pagination.application.post;

import java.util.List;
import com.pagination.application.comment.CommentMapper;
import com.pagination.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final CommentMapper commentMapper;

    public PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                commentMapper.toResponses(post.getComments())
        );
    }

    public List<PostResponse> toResponses(List<Post> posts) {
        return posts.stream()
                .map(this::toResponse)
                .toList();
    }
}
