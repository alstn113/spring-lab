package com.pagination.application;

import java.util.List;
import com.pagination.domain.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogMapper {

    public LogResponse toResponse(Log log) {
        return new LogResponse(
                log.getId(),
                log.getContent()
        );
    }

    public List<LogResponse> toResponses(List<Log> logs) {
        return logs.stream()
                .map(this::toResponse)
                .toList();
    }
}
