package com.engineering_lab.hunger.user.infra.persistence.mapper;

import com.engineering_lab.hunger.user.domain.model.UserActivationDomain;
import com.engineering_lab.hunger.user.infra.persistence.entity.UserActivationJpaEntity;

public final class UserActivationJpaMapper {

    private UserActivationJpaMapper() {
        throw new AssertionError("Utility class must not be instantiated");
    }

    public static UserActivationJpaEntity toEntity(UserActivationDomain activation) {
        return new UserActivationJpaEntity(
                activation.getId(), activation.getUserId(), activation.getTokenHash(),
                activation.getExpiresAt(), activation.getCreatedAt(), activation.getUsedAt());
    }

    public static UserActivationDomain toDomain(UserActivationJpaEntity activation) {
        return UserActivationDomain.rehydrate(
                activation.getId(), activation.getUserId(), activation.getTokenHash(),
                activation.getExpiresAt(), activation.getCreatedAt(), activation.getUsedAt());
    }
}
