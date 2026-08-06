package com.engineering_lab.hunger.invitation.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.engineering_lab.hunger.membership.domain.model.MembershipRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "invitations")
@Getter
public class InvitationJpaEntity {
    @Id @Generated @ColumnDefault("uuidv7()")
    @Column(name = "invitation_id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;
    @Column(name = "created_by_user_id", nullable = false, updatable = false)
    private UUID createdByUserId;
    @Column(name = "email", nullable = false, length = 100, updatable = false)
    private String email;
    @Column(name = "normalized_email", nullable = false, length = 100, updatable = false)
    private String normalizedEmail;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20, updatable = false)
    private MembershipRole role;
    @Column(name = "token_hash", nullable = false, updatable = false)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "accepted_at")
    private Instant acceptedAt;

    protected InvitationJpaEntity() {
    }

    public InvitationJpaEntity(
            UUID id, UUID tenantId, UUID createdByUserId, String email,
            String normalizedEmail, MembershipRole role, String tokenHash,
            Instant expiresAt, Instant createdAt, Instant acceptedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.createdByUserId = createdByUserId;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
        this.role = role;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
    }
}
