package com.engineering_lab.hunger.user.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.user.application.port.UserActivationRepositoryPort;
import com.engineering_lab.hunger.user.domain.model.UserActivationDomain;
import com.engineering_lab.hunger.user.infra.persistence.entity.UserActivationJpaEntity;
import com.engineering_lab.hunger.user.infra.persistence.mapper.UserActivationJpaMapper;
import com.engineering_lab.hunger.user.infra.persistence.repository.UserActivationJpaRepository;

@Repository
public class UserActivationJpaRepositoryAdapter
        implements UserActivationRepositoryPort {

    private final UserActivationJpaRepository repository;

    public UserActivationJpaRepositoryAdapter(UserActivationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserActivationDomain save(UserActivationDomain activation) {
        UserActivationJpaEntity saved = repository.saveAndFlush(
                UserActivationJpaMapper.toEntity(activation));
        return UserActivationJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<UserActivationDomain> findByTokenHashForUpdate(String tokenHash) {
        return repository.findByTokenHashForUpdate(tokenHash)
                .map(UserActivationJpaMapper::toDomain);
    }
}
