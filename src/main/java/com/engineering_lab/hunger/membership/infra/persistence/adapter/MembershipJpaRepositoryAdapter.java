package com.engineering_lab.hunger.membership.infra.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.membership.application.port.MembershipRepositoryPort;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.infra.persistence.entity.MembershipJpaEntity;
import com.engineering_lab.hunger.membership.infra.persistence.mapper.MembershipJpaMapper;
import com.engineering_lab.hunger.membership.infra.persistence.repository.MembershipJpaRepository;

@Repository
public class MembershipJpaRepositoryAdapter
        implements MembershipRepositoryPort {

    private final MembershipJpaRepository repository;

    public MembershipJpaRepositoryAdapter(
            MembershipJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public MembershipDomain save(MembershipDomain membership) {
        MembershipJpaEntity saved = repository.saveAndFlush(
                MembershipJpaMapper.toEntity(membership)
        );

        return MembershipJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<MembershipDomain> findByUserIdAndTenantId(UUID userId, UUID tenantId) {
        return repository.findByUserIdAndTenantId(userId, tenantId)
                .map(MembershipJpaMapper::toDomain);
    }
}
