package com.engineering_lab.hunger.membership.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.engineering_lab.hunger.membership.domain.model.MembershipRole;
import com.engineering_lab.hunger.membership.domain.model.MembershipStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "membership",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_membership_user_tenant",
                columnNames = {"user_id", "tenant_id"}
        ),
        indexes = @Index(name = "idx_membership_tenant_id", columnList = "tenant_id")
)
public class MembershipJpaEntity {
    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "membership_id", nullable = false, updatable = false)
    private UUID membershipId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MembershipRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MembershipJpaEntity() {
    }

    public MembershipJpaEntity(
            UUID membershipId,
            UUID userId,
            UUID tenantId,
            MembershipRole role,
            MembershipStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getMembershipId() {
        return membershipId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public MembershipRole getRole() {
        return role;
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
