package com.engineering_lab.hunger.user_session.infra.entity;

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

@Entity
@Table(
        name = "user_sessions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_sessions_token_hash",
                columnNames = "token_hash"
        ),
        indexes = {
                @Index(name = "idx_user_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_user_sessions_expires_at", columnList = "expires_at")
        }
)
public class UserSessionJpaEntity {
    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "user_session_id", nullable = false, updatable = false)
    private UUID userSessionId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;
}
