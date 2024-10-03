package com.pagination.application;

import java.util.List;

public record ReactionResponse(Long id, String action, List<LogResponse> logs) {
}
