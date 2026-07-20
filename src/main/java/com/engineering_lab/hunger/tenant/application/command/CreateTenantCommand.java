package com.engineering_lab.hunger.tenant.application.command;

import java.util.UUID;

public record CreateTenantCommand(
        UUID creatorUserId,
        String name,
        String tenantCode
) {
}
