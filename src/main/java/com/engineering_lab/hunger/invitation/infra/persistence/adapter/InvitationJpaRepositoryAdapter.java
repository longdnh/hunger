package com.engineering_lab.hunger.invitation.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.invitation.application.port.InvitationRepositoryPort;
import com.engineering_lab.hunger.invitation.domain.model.InvitationDomain;
import com.engineering_lab.hunger.invitation.infra.persistence.entity.InvitationJpaEntity;
import com.engineering_lab.hunger.invitation.infra.persistence.mapper.InvitationJpaMapper;
import com.engineering_lab.hunger.invitation.infra.persistence.repository.InvitationJpaRepository;

@Repository
public class InvitationJpaRepositoryAdapter implements InvitationRepositoryPort {
    private final InvitationJpaRepository repository;
    public InvitationJpaRepositoryAdapter(InvitationJpaRepository repository) {
        this.repository = repository;
    }
    @Override
    public InvitationDomain save(InvitationDomain invitation) {
        InvitationJpaEntity saved = repository.saveAndFlush(InvitationJpaMapper.toEntity(invitation));
        return InvitationJpaMapper.toDomain(saved);
    }
    @Override
    public Optional<InvitationDomain> findByTokenHashForUpdate(String tokenHash) {
        return repository.findByTokenHashForUpdate(tokenHash).map(InvitationJpaMapper::toDomain);
    }
}
