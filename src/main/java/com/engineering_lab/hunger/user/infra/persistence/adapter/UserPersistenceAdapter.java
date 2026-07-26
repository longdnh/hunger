package com.engineering_lab.hunger.user.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.domain.repository.UserRepository;
import com.engineering_lab.hunger.user.infra.persistence.mapper.UserPersistenceMapper;
import com.engineering_lab.hunger.user.infra.persistence.repository.UserSpringDataRepository;

@Repository
public class UserPersistenceAdapter
        implements UserRepository {

    private final UserSpringDataRepository repository;

    public UserPersistenceAdapter(
            UserSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<UserDomain> findByNormalizedEmail(
            String normalizedEmail) {
        return repository
                .findByNormalizedEmail(normalizedEmail)
                .map(UserPersistenceMapper::toDomain);
    }
}
