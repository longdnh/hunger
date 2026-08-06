package com.engineering_lab.hunger.user.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.engineering_lab.hunger.common.validator.Validator;

import lombok.Getter;

@Getter
public class UserActivationDomain {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant usedAt;

    private UserActivationDomain(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant usedAt
    ) {
        this.id = id;
        this.userId = Validator.requireUuidV7(userId, "userId");
        this.tokenHash = Validator.requireText(tokenHash, "tokenHash", 255);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.usedAt = usedAt;
    }

    public static UserActivationDomain create(
            UUID userId,
            String tokenHash,
            Instant createdAt,
            Instant expiresAt
    ) {
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
        return new UserActivationDomain(null, userId, tokenHash, expiresAt, createdAt, null);
    }

    public static UserActivationDomain rehydrate(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant usedAt
    ) {
        return new UserActivationDomain(
                Validator.requireUuidV7(id, "id"), userId, tokenHash, expiresAt, createdAt, usedAt);
    }

    public boolean isActive(Instant now) {
        return usedAt == null && now.isBefore(expiresAt);
    }

    public void markUsed(Instant now) {
        if (usedAt == null) {
            usedAt = Objects.requireNonNull(now, "now must not be null");
        }
    }
}
