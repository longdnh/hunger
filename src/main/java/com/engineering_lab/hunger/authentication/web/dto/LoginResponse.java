package com.engineering_lab.hunger.authentication.web.dto;

import java.time.Instant;
import java.util.Objects;

import com.engineering_lab.hunger.authentication.application.result.AuthenticationTokens;

public record LoginResponse(
        String accessToken,
        Instant accessTokenExpiresAt) {

    public LoginResponse {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException(
                    "accessToken must not be blank");
        }

        Objects.requireNonNull(
                accessTokenExpiresAt,
                "accessTokenExpiresAt must not be null");
    }

    public static LoginResponse from(AuthenticationTokens result) {
        Objects.requireNonNull(
                result,
                "result must not be null");

        return new LoginResponse(
                result.accessToken(),
                result.accessTokenExpiresAt());
    }

    @Override
    public String toString() {
        return """
                LoginResponse[
                    accessToken=[REDACTED],
                    accessTokenExpiresAt=%s
                ]
                """.formatted(accessTokenExpiresAt);
    }
}
