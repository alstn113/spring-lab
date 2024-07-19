package com.project.auth.application.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.auth.domain.Member;
import com.project.auth.domain.Provider;
import jakarta.annotation.Nullable;

public record SocialProfile(
        Long id,

        String login,

        @JsonProperty("avatar_url")
        String avatarUrl,

        @Nullable
        String email,

        String name
) {

    public Member toMember(Provider provider) {
        return new Member(
                email,
                provider,
                id,
                name,
                avatarUrl
        );
    }
}
