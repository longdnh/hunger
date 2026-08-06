package com.engineering_lab.hunger.user.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.engineering_lab.hunger.user.domain.model.PlatformRole;
import com.engineering_lab.hunger.user.domain.model.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(name = "users", uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_normalized_email", columnNames = "normalized_email")
})
@Getter
public class UserJpaEntity {
        @Id
        @Generated
        @ColumnDefault("uuidv7()")
        @Column(name = "user_id", nullable = false, updatable = false)
        private UUID userId;

        @Column(name = "name", nullable = false, length = 100)
        private String name;

        @Column(name = "email", nullable = false, length = 100)
        private String email;

        @Column(name = "normalized_email", nullable = false, length = 100)
        private String normalizedEmail;

        @Column(name = "password_hash", nullable = false)
        private String passwordHash;

        @Enumerated(EnumType.STRING)
        @Column(name = "platform_role", nullable = false, length = 20)
        private PlatformRole platformRole;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false, length = 30)
        private UserStatus status;

        @Column(name = "email_verified_at")
        private Instant emailVerifiedAt;

        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;

        protected UserJpaEntity() {
        }

        public UserJpaEntity(
                        UUID userId,
                        String name,
                        String email,
                        String normalizedEmail,
                        String passwordHash,
                        PlatformRole platformRole,
                        UserStatus status,
                        Instant emailVerifiedAt,
                        Instant createdAt,
                        Instant updatedAt) {
                this.userId = userId;
                this.name = name;
                this.email = email;
                this.normalizedEmail = normalizedEmail;
                this.passwordHash = passwordHash;
                this.platformRole = platformRole;
                this.status = status;
                this.emailVerifiedAt = emailVerifiedAt;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
        }

}
