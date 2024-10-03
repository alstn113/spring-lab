package com.pagination.application.comment;

import java.util.List;
import com.pagination.application.reaction.ReactionResponse;

public record CommentResponse(Long id, String content, List<ReactionResponse> reactions) {
}
