package com.engineering_lab.hunger.usersession.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.engineering_lab.hunger.usersession.infra.persistence.entity.UserSessionJpaEntity;

import jakarta.persistence.LockModeType;

public interface UserSessionJpaRepository
        extends JpaRepository<UserSessionJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select session
            from UserSessionJpaEntity session
            where session.tokenHash = :tokenHash
            """)
    Optional<UserSessionJpaEntity> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash);

}
