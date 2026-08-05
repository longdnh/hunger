package com.engineering_lab.hunger.user.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import com.engineering_lab.hunger.common.validator.Validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDomain {

    private final UUID id;
    private String name;
    private String email;
    private String normalizedEmail;
    private String passwordHash;
    private PlatformRole platformRole;
    private UserStatus status;
    private Instant emailVerifiedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private UserDomain(
            UUID id,
            String name,
            String email,
            String passwordHash,
            PlatformRole platformRole,
            UserStatus status,
            Instant emailVerifiedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = Validator.requireText(name, "name", 100);
        this.email = Validator.requireEmail(email);
        this.normalizedEmail = this.email.toLowerCase(Locale.ROOT);
        this.passwordHash = Validator.requireText(passwordHash, "passwordHash", 255);
        this.platformRole = Objects.requireNonNull(
                platformRole,
                "platformRole must not be null");
        this.status = Objects.requireNonNull(
                status,
                "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Validator.requireNotBefore(updatedAt, createdAt, "updatedAt");
        this.emailVerifiedAt = emailVerifiedAt == null
                ? null
                : Validator.requireNotBefore(emailVerifiedAt, createdAt, "emailVerifiedAt");
    }

    public static UserDomain createPendingUser(
            String name,
            String email,
            String passwordHash,
            Instant createdAt
    ) {
        return new UserDomain(
                null,
                name,
                email,
                passwordHash,
                PlatformRole.USER,
                UserStatus.PENDING_ACTIVATION,
                null,
                createdAt,
                createdAt);
    }

    public static UserDomain createTopAdmin(
            String name,
            String email,
            String passwordHash,
            Instant createdAt
    ) {
        return new UserDomain(
                null,
                name,
                email,
                passwordHash,
                PlatformRole.TOP_ADMIN,
                UserStatus.ACTIVE,
                createdAt,
                createdAt,
                createdAt);
    }

    public static UserDomain rehydrate(
            UUID id,
            String name,
            String email,
            String passwordHash,
            PlatformRole platformRole,
            UserStatus status,
            Instant emailVerifiedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new UserDomain(
                Validator.requireUuidV7(id, "id"),
                name,
                email,
                passwordHash,
                platformRole,
                status,
                emailVerifiedAt,
                createdAt,
                updatedAt
        );
    }

    public boolean isTopAdmin() {
        return platformRole == PlatformRole.TOP_ADMIN;
    }

    public boolean canAuthenticate() {
        return status == UserStatus.ACTIVE;
    }

    public void grantTopAdmin(Instant changedAt) {
        platformRole = PlatformRole.TOP_ADMIN;
        status = UserStatus.ACTIVE;
        emailVerifiedAt = emailVerifiedAt == null
                ? changedAt
                : emailVerifiedAt;
        updatedAt = Validator.requireNotBefore(
                changedAt,
                updatedAt,
                "changedAt");
    }

    public void activate(
            String newPasswordHash,
            Instant activatedAt
    ) {
        passwordHash = Validator.requireText(
                newPasswordHash,
                "newPasswordHash",
                255);
        status = UserStatus.ACTIVE;
        emailVerifiedAt = activatedAt;
        updatedAt = Validator.requireNotBefore(
                activatedAt,
                updatedAt,
                "activatedAt");
    }
}
