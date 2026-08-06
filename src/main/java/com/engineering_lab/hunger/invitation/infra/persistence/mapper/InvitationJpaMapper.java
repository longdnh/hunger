package com.engineering_lab.hunger.invitation.infra.persistence.mapper;

import com.engineering_lab.hunger.invitation.domain.model.InvitationDomain;
import com.engineering_lab.hunger.invitation.infra.persistence.entity.InvitationJpaEntity;

public final class InvitationJpaMapper {
    private InvitationJpaMapper() {
        throw new AssertionError("Utility class must not be instantiated");
    }
    public static InvitationJpaEntity toEntity(InvitationDomain i) {
        return new InvitationJpaEntity(
                i.getId(), i.getTenantId(), i.getCreatedByUserId(), i.getEmail(),
                i.getNormalizedEmail(), i.getRole(), i.getTokenHash(),
                i.getExpiresAt(), i.getCreatedAt(), i.getAcceptedAt());
    }
    public static InvitationDomain toDomain(InvitationJpaEntity i) {
        return InvitationDomain.rehydrate(
                i.getId(), i.getTenantId(), i.getCreatedByUserId(), i.getEmail(),
                i.getRole(), i.getTokenHash(), i.getExpiresAt(), i.getCreatedAt(), i.getAcceptedAt());
    }
}
