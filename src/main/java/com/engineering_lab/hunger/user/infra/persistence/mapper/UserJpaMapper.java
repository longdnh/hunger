package com.engineering_lab.hunger.user.infra.persistence.mapper;

import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.infra.persistence.entity.UserJpaEntity;

public final class UserJpaMapper {

    private UserJpaMapper() {
        throw new AssertionError(
                "Utility class must not be instantiated");
    }

    public static UserJpaEntity toEntity(
            UserDomain user
    ) {
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNormalizedEmail(),
                user.getPasswordHash(),
                user.getEmailVerifiedAt(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public static UserDomain toDomain(
            UserJpaEntity user) {
        return UserDomain.rehydrate(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getEmailVerifiedAt(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
