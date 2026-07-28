package com.engineering_lab.hunger.tenant.application.result;

import java.time.Instant;
import java.util.UUID;

public record CreateTenantResult(
        UUID tenantId,
        String name,
        String tenantCode,
        Instant createdAt
) {
}
