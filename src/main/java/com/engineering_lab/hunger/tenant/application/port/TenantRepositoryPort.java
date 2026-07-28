package com.engineering_lab.hunger.tenant.application.port;

import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;

public interface TenantRepositoryPort {

    boolean existsByTenantCode(String tenantCode);

    TenantDomain save(TenantDomain tenant);
}
