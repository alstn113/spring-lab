package com.project.auth.infra.security;

public record Accessor(Long id) {

    private static final Long GUEST_ID = -1L;
    public static final Accessor GUEST = new Accessor(GUEST_ID);

    public boolean isGuest() {
        return GUEST_ID.equals(id);
    }
}
