package com.engineering_lab.hunger.membership.infra.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.engineering_lab.hunger.membership.infra.persistence.entity.MembershipJpaEntity;

public interface MembershipJpaRepository
        extends JpaRepository<MembershipJpaEntity, UUID> {
}
