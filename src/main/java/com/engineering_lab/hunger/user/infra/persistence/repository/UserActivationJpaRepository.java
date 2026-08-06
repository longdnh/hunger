package com.engineering_lab.hunger.user.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.engineering_lab.hunger.user.infra.persistence.entity.UserActivationJpaEntity;

import jakarta.persistence.LockModeType;

public interface UserActivationJpaRepository
        extends JpaRepository<UserActivationJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select activation from UserActivationJpaEntity activation where activation.tokenHash = :tokenHash")
    Optional<UserActivationJpaEntity> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash);
}
