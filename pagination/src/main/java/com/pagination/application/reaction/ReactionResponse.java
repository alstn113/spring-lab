package com.pagination.application.reaction;

import java.util.List;
import com.pagination.application.Log.LogResponse;

public record ReactionResponse(Long id, String action, List<LogResponse> logs) {
}
