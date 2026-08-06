package com.engineering_lab.hunger.membership.application.port;

import java.util.Optional;
import java.util.UUID;

import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;

public interface MembershipRepositoryPort {

    MembershipDomain save(MembershipDomain membership);

    Optional<MembershipDomain> findByUserIdAndTenantId(UUID userId, UUID tenantId);
}
