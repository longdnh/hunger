package com.engineering_lab.hunger.user.domain.repository;

import java.util.Optional;

import com.engineering_lab.hunger.user.domain.model.UserDomain;

public interface UserRepository {

    Optional<UserDomain> findByNormalizedEmail(
            String normalizedEmail);
}
