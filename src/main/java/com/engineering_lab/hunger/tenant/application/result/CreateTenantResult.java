package com.engineering_lab.hunger.tenant.application.result;

import java.time.Instant;
import java.util.UUID;

public record CreateTenantResult(
        UUID tenantId,
        String name,
        String tenantCode,
        Instant createdAt,
        UUID companyAdminUserId,
        String companyAdminEmail,
        String companyAdminActivationToken,
        Instant companyAdminActivationExpiresAt
) {

    @Override
    public String toString() {
        return "CreateTenantResult[tenantId=" + tenantId
                + ", name=" + name
                + ", tenantCode=" + tenantCode
                + ", createdAt=" + createdAt
                + ", companyAdminUserId=" + companyAdminUserId
                + ", companyAdminEmail=[REDACTED]"
                + ", companyAdminActivationToken=[REDACTED]"
                + ", companyAdminActivationExpiresAt="
                + companyAdminActivationExpiresAt + "]";
    }
}
