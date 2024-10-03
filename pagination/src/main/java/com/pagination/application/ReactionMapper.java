package com.pagination.application;

import java.util.List;
import com.pagination.domain.Reaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionMapper {

    private final LogMapper logMapper;

    public ReactionResponse toResponse(Reaction reaction) {
        return new ReactionResponse(
                reaction.getId(),
                reaction.getAction(),
                logMapper.toResponses(reaction.getLogs())
        );
    }

    public List<ReactionResponse> toResponses(List<Reaction> reactions) {
        return reactions.stream()
                .map(this::toResponse)
                .toList();
    }
}
