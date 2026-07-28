package com.engineering_lab.hunger.user.application.port;

import java.util.Optional;

import com.engineering_lab.hunger.user.domain.model.UserDomain;

public interface UserRepositoryPort {

    Optional<UserDomain> findByNormalizedEmail(
            String normalizedEmail);
}
