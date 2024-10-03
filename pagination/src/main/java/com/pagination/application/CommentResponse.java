package com.pagination.application;

import java.util.List;

public record CommentResponse(Long id, String content, List<ReactionResponse> reactions) {
}
