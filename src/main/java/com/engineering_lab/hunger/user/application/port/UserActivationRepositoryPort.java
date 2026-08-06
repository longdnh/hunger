package com.engineering_lab.hunger.user.application.port;

import java.util.Optional;

import com.engineering_lab.hunger.user.domain.model.UserActivationDomain;

public interface UserActivationRepositoryPort {

    UserActivationDomain save(UserActivationDomain activation);

    Optional<UserActivationDomain> findByTokenHashForUpdate(
            String tokenHash);
}
