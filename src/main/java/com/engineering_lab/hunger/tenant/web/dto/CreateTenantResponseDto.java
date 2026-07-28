package com.engineering_lab.hunger.tenant.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.tenant.application.result.CreateTenantResult;

public record CreateTenantResponseDto(
        UUID tenantId,
        String name,
        String tenantCode,
        Instant createdAt
) {
    public static CreateTenantResponseDto from(
            CreateTenantResult tenant
    ) {
        return new CreateTenantResponseDto(
                tenant.tenantId(),
                tenant.name(),
                tenant.tenantCode(),
                tenant.createdAt()
        );
    }
}
