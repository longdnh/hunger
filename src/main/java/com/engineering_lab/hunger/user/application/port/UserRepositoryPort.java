package com.engineering_lab.hunger.user.application.port;

import java.util.Optional;
import java.util.UUID;

import com.engineering_lab.hunger.user.domain.model.UserDomain;

public interface UserRepositoryPort {

    boolean existsByNormalizedEmail(
            String normalizedEmail);

    Optional<UserDomain> findByNormalizedEmail(
            String normalizedEmail);

    Optional<UserDomain> findById(UUID userId);

    UserDomain save(UserDomain user);
}
