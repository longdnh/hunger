package com.engineering_lab.hunger.invitation.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.membership.domain.model.MembershipRole;

import lombok.Getter;

@Getter
public class InvitationDomain {

    private final UUID id;
    private final UUID tenantId;
    private final UUID createdByUserId;
    private final String email;
    private final String normalizedEmail;
    private final MembershipRole role;
    private final String tokenHash;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant acceptedAt;

    private InvitationDomain(
            UUID id,
            UUID tenantId,
            UUID createdByUserId,
            String email,
            MembershipRole role,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant acceptedAt
    ) {
        this.id = id;
        this.tenantId = Validator.requireUuidV7(tenantId, "tenantId");
        this.createdByUserId = Validator.requireUuidV7(createdByUserId, "createdByUserId");
        this.email = Validator.requireEmail(email);
        this.normalizedEmail = Validator.normalizeEmail(email);
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.tokenHash = Validator.requireText(tokenHash, "tokenHash", 255);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.acceptedAt = acceptedAt;
    }

    public static InvitationDomain create(
            UUID tenantId,
            UUID createdByUserId,
            String email,
            MembershipRole role,
            String tokenHash,
            Instant createdAt,
            Instant expiresAt
    ) {
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
        return new InvitationDomain(
                null, tenantId, createdByUserId, email, role,
                tokenHash, expiresAt, createdAt, null);
    }

    public static InvitationDomain rehydrate(
            UUID id,
            UUID tenantId,
            UUID createdByUserId,
            String email,
            MembershipRole role,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant acceptedAt
    ) {
        return new InvitationDomain(
                Validator.requireUuidV7(id, "id"), tenantId, createdByUserId,
                email, role, tokenHash, expiresAt, createdAt, acceptedAt);
    }

    public boolean isActive(Instant now) {
        return acceptedAt == null && now.isBefore(expiresAt);
    }

    public void accept(Instant now) {
        if (!isActive(now)) {
            throw new IllegalStateException("Invitation is not active");
        }
        acceptedAt = now;
    }
}
