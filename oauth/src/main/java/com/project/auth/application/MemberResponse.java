package com.project.auth.application;

import com.project.auth.domain.Member;

public record MemberResponse(Long id, String email, String name, String imageUrl) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getImageUrl()
        );
    }
}
