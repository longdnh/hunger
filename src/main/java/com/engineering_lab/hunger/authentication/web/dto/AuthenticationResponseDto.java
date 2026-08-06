package com.engineering_lab.hunger.authentication.web.dto;

import java.time.Instant;
import java.util.Objects;

import com.engineering_lab.hunger.authentication.application.result.AuthenticationResult;

public record AuthenticationResponseDto(
        String accessToken,
        Instant accessTokenExpiresAt
) {

    public AuthenticationResponseDto {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException(
                    "accessToken must not be blank");
        }

        Objects.requireNonNull(
                accessTokenExpiresAt,
                "accessTokenExpiresAt must not be null");
    }

    public static AuthenticationResponseDto from(
            AuthenticationResult result
    ) {
        Objects.requireNonNull(
                result,
                "result must not be null");

        return new AuthenticationResponseDto(
                result.accessToken(),
                result.accessTokenExpiresAt());
    }

    @Override
    public String toString() {
        return """
                AuthenticationResponseDto[
                    accessToken=[REDACTED],
                    accessTokenExpiresAt=%s
                ]
                """.formatted(accessTokenExpiresAt);
    }
}
