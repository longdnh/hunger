package com.engineering_lab.hunger.membership.infra.persistence.adapter;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.domain.repository.MembershipRepository;
import com.engineering_lab.hunger.membership.infra.persistence.entity.MembershipJpaEntity;
import com.engineering_lab.hunger.membership.infra.persistence.mapper.MembershipPersistenceMapper;
import com.engineering_lab.hunger.membership.infra.persistence.repository.MembershipSpringDataRepository;

@Repository
public class MembershipPersistenceAdapter implements MembershipRepository {
    private final MembershipSpringDataRepository repository;

    public MembershipPersistenceAdapter(MembershipSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public MembershipDomain save(MembershipDomain membership) {
        MembershipJpaEntity saved = repository.saveAndFlush(
                MembershipPersistenceMapper.toEntity(membership)
        );
        return MembershipPersistenceMapper.toDomain(saved);
    }
}
