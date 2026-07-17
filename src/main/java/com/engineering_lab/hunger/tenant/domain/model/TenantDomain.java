package com.engineering_lab.hunger.tenant.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.engineering_lab.hunger.common.validator.Validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantDomain {

    private final UUID id;
    private String name;
    private final String tenantCode;
    private final Instant createdAt;
    private Instant updatedAt;

    private TenantDomain(
            UUID id,
            String name,
            String tenantCode,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = Validator.requireText(name, "name", 100);
        this.tenantCode = Validator.normalizeTenantCode(tenantCode);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Validator.requireNotBefore(updatedAt, createdAt, "updatedAt");
    }

    public static TenantDomain create(String name, String tenantCode, Instant createdAt) {
        return new TenantDomain(null, name, tenantCode, createdAt, createdAt);
    }

    public static TenantDomain rehydrate(
            UUID id,
            String name,
            String tenantCode,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new TenantDomain(
                Validator.requireUuidV7(id, "id"),
                name,
                tenantCode,
                createdAt,
                updatedAt
        );
    }

    public void rename(String newName, Instant changedAt) {
        name = Validator.requireText(newName, "newName", 100);
        updatedAt = Validator.requireNotBefore(changedAt, updatedAt, "changedAt");
    }
}
