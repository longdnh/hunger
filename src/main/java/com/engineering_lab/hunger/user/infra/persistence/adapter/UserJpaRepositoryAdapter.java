package com.engineering_lab.hunger.user.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.infra.persistence.mapper.UserJpaMapper;
import com.engineering_lab.hunger.user.infra.persistence.repository.UserJpaRepository;

@Repository
public class UserJpaRepositoryAdapter
        implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserJpaRepositoryAdapter(
            UserJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<UserDomain> findByNormalizedEmail(
            String normalizedEmail) {
        return repository
                .findByNormalizedEmail(normalizedEmail)
                .map(UserJpaMapper::toDomain);
    }
}
