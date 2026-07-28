package com.engineering_lab.hunger.usersession.application.port;

import java.util.Optional;

import com.engineering_lab.hunger.usersession.domain.model.UserSessionDomain;

public interface UserSessionRepositoryPort {

    UserSessionDomain save(UserSessionDomain session);

    Optional<UserSessionDomain> findByTokenHashForUpdate(
            String tokenHash);
}
