package com.engineering_lab.hunger.user.application.result;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.user.domain.model.UserDomain;

public record UserResult(
        UUID userId,
        String name,
        String email,
        Instant emailVerifiedAt,
        Instant createdAt
) {

    public static UserResult from(UserDomain user) {
        return new UserResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getEmailVerifiedAt(),
                user.getCreatedAt());
    }
}
