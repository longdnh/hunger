package com.engineering_lab.hunger.tenant.infra.persistence.adapter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.tenant.application.port.TenantRepositoryPort;
import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.tenant.infra.persistence.entity.TenantJpaEntity;
import com.engineering_lab.hunger.tenant.infra.persistence.mapper.TenantJpaMapper;
import com.engineering_lab.hunger.tenant.infra.persistence.repository.TenantJpaRepository;

@Repository
public class TenantJpaRepositoryAdapter
        implements TenantRepositoryPort {

    private final TenantJpaRepository repository;

    public TenantJpaRepositoryAdapter(
            TenantJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByTenantCode(String tenantCode) {
        return repository.existsByTenantCode(tenantCode);
    }

    @Override
    public TenantDomain save(TenantDomain tenant) {
        try {
            TenantJpaEntity saved = repository.saveAndFlush(
                    TenantJpaMapper.toEntity(tenant));

            return TenantJpaMapper.toDomain(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new TenantCodeAlreadyExistsException(
                    tenant.getTenantCode());
        }
    }
}
