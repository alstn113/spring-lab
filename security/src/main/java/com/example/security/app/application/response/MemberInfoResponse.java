package com.example.security.app.application.response;

import com.example.security.app.domain.Role;

public record MemberInfoResponse(
        Long memberId,
        String username,
        Role role
) {
}
