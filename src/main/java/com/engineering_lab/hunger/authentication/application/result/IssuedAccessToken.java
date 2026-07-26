package com.engineering_lab.hunger.authentication.application.result;

import java.time.Instant;
import java.util.Objects;

public record IssuedAccessToken(
        String token,
        Instant expiresAt) {

    public IssuedAccessToken {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "token must not be blank");
        }

        Objects.requireNonNull(
                expiresAt,
                "expiresAt must not be null");
    }
}
