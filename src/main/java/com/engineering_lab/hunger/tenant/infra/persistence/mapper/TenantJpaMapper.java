package com.engineering_lab.hunger.tenant.infra.persistence.mapper;

import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.tenant.infra.persistence.entity.TenantJpaEntity;

public final class TenantJpaMapper {

    private TenantJpaMapper() {
        throw new AssertionError(
                "Utility class must not be instantiated");
    }

    public static TenantJpaEntity toEntity(TenantDomain tenant) {
        return new TenantJpaEntity(
                tenant.getId(),
                tenant.getName(),
                tenant.getTenantCode(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }

    public static TenantDomain toDomain(TenantJpaEntity tenant) {
        return TenantDomain.rehydrate(
                tenant.getTenantId(),
                tenant.getTenantName(),
                tenant.getTenantCode(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }
}
