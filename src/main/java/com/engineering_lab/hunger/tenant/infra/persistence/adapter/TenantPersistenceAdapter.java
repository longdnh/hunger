package com.engineering_lab.hunger.tenant.infra.persistence.adapter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.tenant.domain.repository.TenantRepository;
import com.engineering_lab.hunger.tenant.infra.persistence.entity.TenantJpaEntity;
import com.engineering_lab.hunger.tenant.infra.persistence.mapper.TenantPersistenceMapper;
import com.engineering_lab.hunger.tenant.infra.persistence.repository.TenantSpringDataRepository;

@Repository
public class TenantPersistenceAdapter implements TenantRepository {
    private final TenantSpringDataRepository repository;

    public TenantPersistenceAdapter(TenantSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByTenantCode(String tenantCode) {
        return repository.existsByTenantCode(tenantCode);
    }

    @Override
    public TenantDomain save(TenantDomain tenant) {
        try {
            TenantJpaEntity saved = repository.saveAndFlush(TenantPersistenceMapper.toEntity(tenant));
            return TenantPersistenceMapper.toDomain(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new TenantCodeAlreadyExistsException(tenant.getTenantCode());
        }
    }
}
