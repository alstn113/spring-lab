package com.pagination.application.post;

import java.util.List;
import com.pagination.application.comment.CommentResponse;

public record PostResponse(Long id, String title, List<CommentResponse> comments) {
}
