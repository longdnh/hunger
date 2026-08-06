package com.engineering_lab.hunger.invitation.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.engineering_lab.hunger.invitation.infra.persistence.entity.InvitationJpaEntity;

import jakarta.persistence.LockModeType;

public interface InvitationJpaRepository extends JpaRepository<InvitationJpaEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvitationJpaEntity i where i.tokenHash = :tokenHash")
    Optional<InvitationJpaEntity> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);
}
