package com.engineering_lab.hunger.tenant.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "tenant",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tenant_tenant_code",
                columnNames = "tenant_code"
        )
)
public class TenantJpaEntity {
    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false, length = 100)
    private String tenantName;

    @Column(name = "tenant_code", nullable = false, length = 5)
    private String tenantCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TenantJpaEntity() {
    }

    public TenantJpaEntity(
            UUID tenantId,
            String tenantName,
            String tenantCode,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantCode = tenantCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
