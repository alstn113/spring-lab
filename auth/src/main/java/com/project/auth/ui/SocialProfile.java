package com.project.auth.ui;

import jakarta.annotation.Nullable;

public record SocialProfile(long id, String login, String avatar_url, @Nullable String email, String name) {
}
