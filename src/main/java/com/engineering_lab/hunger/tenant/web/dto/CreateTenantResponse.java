package com.engineering_lab.hunger.tenant.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.tenant.application.result.CreatedTenant;

public record CreateTenantResponse(
        UUID tenantId,
        String name,
        String tenantCode,
        Instant createdAt
) {
    public static CreateTenantResponse from(CreatedTenant tenant) {
        return new CreateTenantResponse(
                tenant.tenantId(),
                tenant.name(),
                tenant.tenantCode(),
                tenant.createdAt()
        );
    }
}
