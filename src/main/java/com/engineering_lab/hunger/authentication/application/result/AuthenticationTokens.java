package com.engineering_lab.hunger.authentication.application.result;

import java.time.Instant;
import java.util.Objects;

public final class AuthenticationTokens {

    private final String accessToken;
    private final Instant accessTokenExpiresAt;
    private final String refreshToken;
    private final Instant refreshTokenExpiresAt;

    public AuthenticationTokens(
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException(
                    "accessToken must not be blank");
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException(
                    "refreshToken must not be blank");
        }

        this.accessToken = accessToken;
        this.accessTokenExpiresAt = Objects.requireNonNull(
                accessTokenExpiresAt,
                "accessTokenExpiresAt must not be null");
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = Objects.requireNonNull(
                refreshTokenExpiresAt,
                "refreshTokenExpiresAt must not be null");
    }

    public String accessToken() {
        return accessToken;
    }

    public Instant accessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public String refreshToken() {
        return refreshToken;
    }

    public Instant refreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }
}
