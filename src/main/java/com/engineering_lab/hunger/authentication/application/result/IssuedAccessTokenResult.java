package com.engineering_lab.hunger.authentication.application.result;

import java.time.Instant;
import java.util.Objects;

public record IssuedAccessTokenResult(
        String token,
        Instant expiresAt
) {

    public IssuedAccessTokenResult {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "token must not be blank");
        }

        Objects.requireNonNull(
                expiresAt,
                "expiresAt must not be null");
    }

    @Override
    public String toString() {
        return "IssuedAccessTokenResult[token=[REDACTED], expiresAt="
                + expiresAt + "]";
    }
}
