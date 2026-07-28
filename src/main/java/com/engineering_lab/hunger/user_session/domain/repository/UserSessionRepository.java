package com.engineering_lab.hunger.user_session.domain.repository;

import java.util.Optional;

import com.engineering_lab.hunger.user_session.domain.model.UserSessionDomain;

public interface UserSessionRepository {

    UserSessionDomain save(UserSessionDomain session);

    Optional<UserSessionDomain> findByTokenHashForUpdate(String tokenHash);
}
