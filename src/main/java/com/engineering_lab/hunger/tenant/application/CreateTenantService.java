package com.engineering_lab.hunger.tenant.application;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.domain.repository.MembershipRepository;
import com.engineering_lab.hunger.tenant.api.CreateTenantUseCase;
import com.engineering_lab.hunger.tenant.application.command.CreateTenantCommand;
import com.engineering_lab.hunger.tenant.application.result.CreatedTenant;
import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.tenant.domain.repository.TenantRepository;

@Service
public class CreateTenantService implements CreateTenantUseCase {
    private final TenantRepository tenantRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;

    public CreateTenantService(
            TenantRepository tenantRepository,
            MembershipRepository membershipRepository,
            Clock clock
    ) {
        this.tenantRepository = tenantRepository;
        this.membershipRepository = membershipRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CreatedTenant execute(CreateTenantCommand command) {
        String tenantCode = Validator.normalizeTenantCode(command.tenantCode());
        if (tenantRepository.existsByTenantCode(tenantCode)) {
            throw new TenantCodeAlreadyExistsException(tenantCode);
        }

        Instant now = clock.instant();
        TenantDomain tenant = TenantDomain.create(command.name(), tenantCode, now);
        TenantDomain savedTenant = tenantRepository.save(tenant);

        MembershipDomain ownerMembership = MembershipDomain.createOwner(
                command.creatorUserId(),
                savedTenant.getId(),
                now
        );
        membershipRepository.save(ownerMembership);

        return new CreatedTenant(
                savedTenant.getId(),
                savedTenant.getName(),
                savedTenant.getTenantCode(),
                savedTenant.getCreatedAt()
        );
    }
}
