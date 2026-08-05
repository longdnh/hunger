package com.engineering_lab.hunger.user.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.user.application.result.UserResult;
import com.engineering_lab.hunger.user.domain.model.PlatformRole;
import com.engineering_lab.hunger.user.domain.model.UserStatus;

public record UserResponseDto(
        UUID userId,
        String name,
        String email,
        PlatformRole platformRole,
        UserStatus status,
        Instant emailVerifiedAt,
        Instant createdAt) {

    public static UserResponseDto from(UserResult user) {
        return new UserResponseDto(
                user.userId(),
                user.name(),
                user.email(),
                user.platformRole(),
                user.status(),
                user.emailVerifiedAt(),
                user.createdAt());
    }
}
