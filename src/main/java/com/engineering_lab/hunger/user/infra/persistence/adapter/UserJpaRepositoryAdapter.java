package com.engineering_lab.hunger.user.infra.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.user.application.exception.EmailAlreadyExistsException;
import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.infra.persistence.entity.UserJpaEntity;
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
    public boolean existsByNormalizedEmail(
            String normalizedEmail
    ) {
        return repository.existsByNormalizedEmail(
                normalizedEmail);
    }

    @Override
    public Optional<UserDomain> findByNormalizedEmail(
            String normalizedEmail) {
        return repository
                .findByNormalizedEmail(normalizedEmail)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<UserDomain> findById(UUID userId) {
        return repository
                .findById(userId)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public UserDomain save(UserDomain user) {
        try {
            UserJpaEntity saved = repository.saveAndFlush(
                    UserJpaMapper.toEntity(user));

            return UserJpaMapper.toDomain(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyExistsException();
        }
    }
}
