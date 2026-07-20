package com.engineering_lab.hunger.membership.infra.persistence.mapper;

import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.infra.persistence.entity.MembershipJpaEntity;

public final class MembershipPersistenceMapper {
    private MembershipPersistenceMapper() {
    }

    public static MembershipJpaEntity toEntity(MembershipDomain membership) {
        return new MembershipJpaEntity(
                membership.getId(),
                membership.getUserId(),
                membership.getTenantId(),
                membership.getRole(),
                membership.getStatus(),
                membership.getCreatedAt(),
                membership.getUpdatedAt()
        );
    }

    public static MembershipDomain toDomain(MembershipJpaEntity membership) {
        return MembershipDomain.rehydrate(
                membership.getMembershipId(),
                membership.getUserId(),
                membership.getTenantId(),
                membership.getRole(),
                membership.getStatus(),
                membership.getCreatedAt(),
                membership.getUpdatedAt()
        );
    }
}
