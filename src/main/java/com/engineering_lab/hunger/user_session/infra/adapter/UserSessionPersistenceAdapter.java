package com.engineering_lab.hunger.user_session.infra.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.user_session.domain.model.UserSessionDomain;
import com.engineering_lab.hunger.user_session.domain.repository.UserSessionRepository;
import com.engineering_lab.hunger.user_session.infra.entity.UserSessionJpaEntity;
import com.engineering_lab.hunger.user_session.infra.mapper.UserSessionPersistenceMapper;
import com.engineering_lab.hunger.user_session.infra.repository.UserSessionSpringDataRepository;

@Repository
public class UserSessionPersistenceAdapter
        implements UserSessionRepository {

    private final UserSessionSpringDataRepository repository;

    public UserSessionPersistenceAdapter(
            UserSessionSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserSessionDomain save(
            UserSessionDomain session) {
        UserSessionJpaEntity saved = repository.saveAndFlush(
                UserSessionPersistenceMapper.toEntity(session));

        return UserSessionPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<UserSessionDomain> findByTokenHash(
            String tokenHash) {
        return repository
                .findByTokenHash(tokenHash)
                .map(UserSessionPersistenceMapper::toDomain);
    }
}
