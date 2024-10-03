package com.pagination.application;

import java.util.List;

public record PostResponse(Long id, String title, List<CommentResponse> comments) {
}
