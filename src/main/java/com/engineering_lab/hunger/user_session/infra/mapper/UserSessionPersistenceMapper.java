package com.engineering_lab.hunger.user_session.infra.mapper;

import com.engineering_lab.hunger.user_session.domain.model.UserSessionDomain;
import com.engineering_lab.hunger.user_session.infra.entity.UserSessionJpaEntity;

public final class UserSessionPersistenceMapper {

    private UserSessionPersistenceMapper() {
        throw new AssertionError(
                "Utility class must not be instantiated");
    }

    public static UserSessionJpaEntity toEntity(
            UserSessionDomain session) {
        return new UserSessionJpaEntity(
                session.getId(),
                session.getUserId(),
                session.getTokenHash(),
                session.getExpiresAt(),
                session.getRevokedAt());
    }

    public static UserSessionDomain toDomain(
            UserSessionJpaEntity session) {
        return UserSessionDomain.rehydrate(
                session.getUserSessionId(),
                session.getUserId(),
                session.getTokenHash(),
                session.getExpiresAt(),
                session.getRevokedAt());
    }
}
