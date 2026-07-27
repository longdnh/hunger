package com.engineering_lab.hunger.user_session.infra.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.engineering_lab.hunger.user_session.infra.entity.UserSessionJpaEntity;

public interface UserSessionSpringDataRepository
        extends JpaRepository<UserSessionJpaEntity, UUID> {

    Optional<UserSessionJpaEntity> findByTokenHash(String tokenHash);
}
