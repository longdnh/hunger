package com.engineering_lab.hunger.tenant.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity()
@Table(name = "tenant")
public class TenantJpaEntity {
    @Id
    private UUID tenant_id;

    @Column(name = "name", nullable = false, length = 100)
    private String tenant_name;

    @Column(name="tenant_code", nullable = false, unique = true, length = 5)
    private String tenant_code;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;

    @Column(name = "updated_at", nullable = false)
    private Instant updated_at;
}
