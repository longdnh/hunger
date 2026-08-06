package com.engineering_lab.hunger.tenant.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.membership.application.port.MembershipRepositoryPort;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.tenant.application.exception.TopAdminRequiredException;
import com.engineering_lab.hunger.tenant.application.port.TenantRepositoryPort;
import com.engineering_lab.hunger.tenant.application.result.CreateTenantResult;
import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.user.application.UserService;
import com.engineering_lab.hunger.user.application.exception.UserNotFoundException;
import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.application.result.CompanyAdminProvisionResult;
import com.engineering_lab.hunger.user.domain.model.UserDomain;

@Service
public class TenantService {

    private final TenantRepositoryPort tenantRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final UserRepositoryPort userRepository;
    private final UserService userService;
    private final Clock clock;

    public TenantService(
            TenantRepositoryPort tenantRepository,
            MembershipRepositoryPort membershipRepository,
            UserRepositoryPort userRepository,
            UserService userService,
            Clock clock
    ) {
        this.tenantRepository = tenantRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.clock = clock;
    }

    @Transactional
    public CreateTenantResult create(
            UUID creatorUserId,
            String name,
            String rawTenantCode,
            String companyAdminName,
            String companyAdminEmail
    ) {
        requireTopAdmin(creatorUserId);

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

        CompanyAdminProvisionResult companyAdmin =
                userService.provisionCompanyAdmin(
                        companyAdminName,
                        companyAdminEmail);

        MembershipDomain adminMembership = MembershipDomain.createAdmin(
                companyAdmin.user().userId(),
                savedTenant.getId(),
                now);
        membershipRepository.save(adminMembership);

        return new CreateTenantResult(
                savedTenant.getId(),
                savedTenant.getName(),
                savedTenant.getTenantCode(),
                savedTenant.getCreatedAt(),
                companyAdmin.user().userId(),
                companyAdmin.user().email(),
                companyAdmin.activationToken(),
                companyAdmin.activationExpiresAt()
        );
    }

    private void requireTopAdmin(UUID userId) {
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isTopAdmin() || !user.canAuthenticate()) {
            throw new TopAdminRequiredException();
        }
    }
}
