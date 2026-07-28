package com.engineering_lab.hunger.tenant.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.membership.application.port.MembershipRepositoryPort;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.tenant.application.port.TenantRepositoryPort;
import com.engineering_lab.hunger.tenant.application.result.CreateTenantResult;
import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;

@Service
public class TenantService {

    private final TenantRepositoryPort tenantRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final Clock clock;

    public TenantService(
            TenantRepositoryPort tenantRepository,
            MembershipRepositoryPort membershipRepository,
            Clock clock
    ) {
        this.tenantRepository = tenantRepository;
        this.membershipRepository = membershipRepository;
        this.clock = clock;
    }

    @Transactional
    public CreateTenantResult create(
            UUID creatorUserId,
            String name,
            String rawTenantCode
    ) {
        String tenantCode = Validator.normalizeTenantCode(
                rawTenantCode);

        if (tenantRepository.existsByTenantCode(tenantCode)) {
            throw new TenantCodeAlreadyExistsException(tenantCode);
        }

        Instant now = clock.instant();
        TenantDomain tenant = TenantDomain.create(
                name,
                tenantCode,
                now);

        TenantDomain savedTenant = tenantRepository.save(tenant);

        MembershipDomain ownerMembership = MembershipDomain.createOwner(
                creatorUserId,
                savedTenant.getId(),
                now
        );
        membershipRepository.save(ownerMembership);

        return new CreateTenantResult(
                savedTenant.getId(),
                savedTenant.getName(),
                savedTenant.getTenantCode(),
                savedTenant.getCreatedAt()
        );
    }
}
