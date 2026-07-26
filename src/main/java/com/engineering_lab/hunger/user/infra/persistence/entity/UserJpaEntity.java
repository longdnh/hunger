package com.engineering_lab.hunger.user.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
                        Instant emailVerifiedAt,
                        Instant createdAt,
                        Instant updatedAt) {
                this.userId = userId;
                this.name = name;
                this.email = email;
                this.normalizedEmail = normalizedEmail;
                this.passwordHash = passwordHash;
                this.emailVerifiedAt = emailVerifiedAt;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
        }

}
