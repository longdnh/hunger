package com.engineering_lab.hunger.user.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.engineering_lab.hunger.user.infra.persistence.entity.UserJpaEntity;

public interface UserSpringDataRepository
                extends JpaRepository<UserJpaEntity, UUID> {

        Optional<UserJpaEntity> findByNormalizedEmail(
                        String normalizedEmail);
}
