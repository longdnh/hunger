package com.engineering_lab.hunger.user.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.user.application.result.UserResult;

public record UserResponseDto(
        UUID userId,
        String name,
        String email,
        Instant emailVerifiedAt,
        Instant createdAt
) {

    public static UserResponseDto from(UserResult user) {
        return new UserResponseDto(
                user.userId(),
                user.name(),
                user.email(),
                user.emailVerifiedAt(),
                user.createdAt());
    }
}
