package com.engineering_lab.hunger.user_session.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.engineering_lab.hunger.common.validator.Validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionDomain {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private Instant revokedAt;

    private UserSessionDomain(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant revokedAt
    ) {
        this.id = id;
        this.userId = Validator.requireUuidV7(userId, "userId");
        this.tokenHash = Validator.requireText(tokenHash, "tokenHash", 255);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.revokedAt = revokedAt;
    }

    public static UserSessionDomain create(
            UUID userId,
            String tokenHash,
            Instant issuedAt,
            Instant expiresAt
    ) {
        Objects.requireNonNull(issuedAt, "issuedAt must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        if (!expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException("expiresAt must be after issuedAt");
        }
        return new UserSessionDomain(null, userId, tokenHash, expiresAt, null);
    }

    public static UserSessionDomain rehydrate(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant revokedAt
    ) {
        return new UserSessionDomain(
                Validator.requireUuidV7(id, "id"),
                userId,
                tokenHash,
                expiresAt,
                revokedAt
        );
    }

    public void revoke(Instant revokedAt) {
        Objects.requireNonNull(revokedAt, "revokedAt must not be null");
        if (this.revokedAt == null) {
            this.revokedAt = revokedAt;
        }
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        return !now.isBefore(expiresAt);
    }

    public boolean isActive(Instant now) {
        return !isRevoked() && !isExpired(now);
    }
}
