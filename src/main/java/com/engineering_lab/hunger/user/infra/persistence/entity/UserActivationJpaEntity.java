package com.engineering_lab.hunger.user.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
        name = "user_activation_tokens",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_activation_tokens_token_hash",
                columnNames = "token_hash"),
        indexes = @Index(
                name = "idx_user_activation_tokens_user_id",
                columnList = "user_id"))
@Getter
public class UserActivationJpaEntity {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "user_activation_token_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "used_at")
    private Instant usedAt;

    protected UserActivationJpaEntity() {
    }

    public UserActivationJpaEntity(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant usedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.usedAt = usedAt;
    }
}
