package com.engineering_lab.hunger.usersession.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.usersession.application.port.UserSessionRepositoryPort;
import com.engineering_lab.hunger.usersession.domain.model.UserSessionDomain;
import com.engineering_lab.hunger.usersession.infra.persistence.entity.UserSessionJpaEntity;
import com.engineering_lab.hunger.usersession.infra.persistence.mapper.UserSessionJpaMapper;
import com.engineering_lab.hunger.usersession.infra.persistence.repository.UserSessionJpaRepository;

@Repository
public class UserSessionJpaRepositoryAdapter
        implements UserSessionRepositoryPort {

    private final UserSessionJpaRepository repository;

    public UserSessionJpaRepositoryAdapter(
            UserSessionJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public UserSessionDomain save(
            UserSessionDomain session) {
        UserSessionJpaEntity saved = repository.saveAndFlush(
                UserSessionJpaMapper.toEntity(session));

        return UserSessionJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<UserSessionDomain> findByTokenHashForUpdate(
            String tokenHash) {
        return repository
                .findByTokenHashForUpdate(tokenHash)
                .map(UserSessionJpaMapper::toDomain);
    }
}
