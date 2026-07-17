package com.engineering_lab.hunger.membership.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.engineering_lab.hunger.common.validator.Validator;

public class MembershipDomain {

    private final UUID id;
    private final UUID userId;
    private final UUID tenantId;
    private MembershipStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private MembershipDomain(
            UUID id,
            UUID userId,
            UUID tenantId,
            MembershipStatus status,
            Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.userId = Validator.requireUuidV7(userId, "userId");
        this.tenantId = Validator.requireUuidV7(tenantId, "tenantId");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Validator.requireNotBefore(updatedAt, createdAt, "updatedAt");
    }

    public static MembershipDomain create(
            UUID userId,
            UUID tenantId,
            Instant createdAt
    ) {
        return new MembershipDomain(
                null,
                userId,
                tenantId,
                MembershipStatus.ACTIVE,
                createdAt,
                createdAt
        );
    }

    public static MembershipDomain rehydrate(
            UUID id,
            UUID userId,
            UUID tenantId,
            MembershipStatus status,
            Instant createdAt,
        Instant updatedAt
    ) {
        return new MembershipDomain(
                Validator.requireUuidV7(id, "id"),
                userId,
                tenantId,
                status,
                createdAt,
                updatedAt
        );
    }

    public void activate(Instant changedAt) {
        changeStatus(MembershipStatus.ACTIVE, changedAt);
    }

    public void deactivate(Instant changedAt) {
        changeStatus(MembershipStatus.INACTIVE, changedAt);
    }

    public void suspend(Instant changedAt) {
        changeStatus(MembershipStatus.SUSPENDED, changedAt);
    }

    public boolean grantsAccess() {
        return status == MembershipStatus.ACTIVE;
    }

    public boolean isPersisted() {
        return id != null;
    }

    private void changeStatus(MembershipStatus newStatus, Instant changedAt) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        Instant eventTime = Validator.requireNotBefore(changedAt, updatedAt, "changedAt");
        if (status != newStatus) {
            status = newStatus;
            updatedAt = eventTime;
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
