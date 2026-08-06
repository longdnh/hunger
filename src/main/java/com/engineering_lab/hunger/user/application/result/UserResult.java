package com.engineering_lab.hunger.user.application.result;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.domain.model.PlatformRole;
import com.engineering_lab.hunger.user.domain.model.UserStatus;

public record UserResult(
        UUID userId,
        String name,
        String email,
        PlatformRole platformRole,
        UserStatus status,
        Instant emailVerifiedAt,
        Instant createdAt
) {

    public static UserResult from(UserDomain user) {
        return new UserResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPlatformRole(),
                user.getStatus(),
                user.getEmailVerifiedAt(),
                user.getCreatedAt());
    }
}
