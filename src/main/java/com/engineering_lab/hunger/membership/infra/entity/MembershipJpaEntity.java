package com.engineering_lab.hunger.membership.infra.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

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
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
