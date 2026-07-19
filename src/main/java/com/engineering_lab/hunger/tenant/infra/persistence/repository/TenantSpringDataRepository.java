package com.engineering_lab.hunger.tenant.infra.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.engineering_lab.hunger.tenant.infra.persistence.entity.TenantJpaEntity;

public interface TenantSpringDataRepository extends JpaRepository<TenantJpaEntity, UUID> {
    boolean existsByTenantCode(String tenantCode);
}
