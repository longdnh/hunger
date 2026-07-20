package com.engineering_lab.hunger.tenant.domain.repository;

import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;

public interface TenantRepository {
    boolean existsByTenantCode(String tenantCode);

    TenantDomain save(TenantDomain tenant);
}
