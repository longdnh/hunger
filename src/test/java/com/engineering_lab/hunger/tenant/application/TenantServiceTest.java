package com.engineering_lab.hunger.tenant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.engineering_lab.hunger.membership.application.port.MembershipRepositoryPort;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.domain.model.MembershipRole;
import com.engineering_lab.hunger.tenant.application.port.TenantRepositoryPort;
import com.engineering_lab.hunger.tenant.application.result.CreateTenantResult;
import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.user.application.UserService;
import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.application.result.CompanyAdminProvisionResult;
import com.engineering_lab.hunger.user.application.result.UserResult;
import com.engineering_lab.hunger.user.domain.model.PlatformRole;
import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.domain.model.UserStatus;

class TenantServiceTest {
    private static final UUID USER_ID = UUID.fromString(
            "01890f9a-6b7c-7def-8123-456789abcdef"
    );
    private static final UUID TENANT_ID = UUID.fromString(
            "01890f9a-6b7c-7def-9234-56789abcdef0"
    );
    private static final UUID ADMIN_ID = UUID.fromString(
            "01890f9a-6b7c-7def-b456-789abcdef012");
    private static final UUID MEMBERSHIP_ID = UUID.fromString(
            "01890f9a-6b7c-7def-a345-6789abcdef01"
    );
    private static final Instant NOW = Instant.parse("2026-07-19T08:00:00Z");

    @Test
    void createsTenantAndCompanyAdminMembershipInTheSameTransaction() {
        FakeTenantRepositoryPort tenantRepository =
                new FakeTenantRepositoryPort();
        FakeMembershipRepositoryPort membershipRepository =
                new FakeMembershipRepositoryPort();
        TenantService service = service(tenantRepository, membershipRepository);

        CreateTenantResult result = service.create(
                USER_ID,
                "  Engineering Lab  ",
                " hn_01 ",
                "Company Admin",
                "admin@example.com"
        );

        assertEquals(TENANT_ID, result.tenantId());
        assertEquals("Engineering Lab", result.name());
        assertEquals("HN_01", result.tenantCode());
        assertEquals(NOW, result.createdAt());
        assertEquals(ADMIN_ID, membershipRepository.saved.getUserId());
        assertEquals(TENANT_ID, membershipRepository.saved.getTenantId());
        assertEquals(MembershipRole.ADMIN, membershipRepository.saved.getRole());
    }

    @Test
    void rejectsAnExistingNormalizedTenantCodeBeforeWriting() {
        FakeTenantRepositoryPort tenantRepository =
                new FakeTenantRepositoryPort();
        tenantRepository.codeExists = true;
        FakeMembershipRepositoryPort membershipRepository =
                new FakeMembershipRepositoryPort();
        TenantService service = service(tenantRepository, membershipRepository);

        assertThrows(
                TenantCodeAlreadyExistsException.class,
                () -> service.create(
                        USER_ID,
                        "Hunger",
                        "hn_01",
                        "Company Admin",
                        "admin@example.com")
        );
        assertEquals("HN_01", tenantRepository.checkedCode);
        assertNull(tenantRepository.saved);
        assertNull(membershipRepository.saved);
    }

    private TenantService service(
            TenantRepositoryPort tenantRepository,
            MembershipRepositoryPort membershipRepository
    ) {
        UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
        UserService userService = mock(UserService.class);
        UserDomain topAdmin = UserDomain.rehydrate(
                USER_ID, "Top Admin", "top@example.com", "{noop}password",
                PlatformRole.TOP_ADMIN, UserStatus.ACTIVE, NOW, NOW, NOW);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(topAdmin));
        UserResult admin = new UserResult(
                ADMIN_ID, "Company Admin", "admin@example.com",
                PlatformRole.USER, UserStatus.PENDING_ACTIVATION, null, NOW);
        when(userService.provisionCompanyAdmin("Company Admin", "admin@example.com"))
                .thenReturn(new CompanyAdminProvisionResult(admin, "activation", NOW.plusSeconds(3600)));
        return new TenantService(
                tenantRepository, membershipRepository, userRepository, userService,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private static final class FakeTenantRepositoryPort
            implements TenantRepositoryPort {
        private boolean codeExists;
        private String checkedCode;
        private TenantDomain saved;

        @Override
        public boolean existsByTenantCode(String tenantCode) {
            checkedCode = tenantCode;
            return codeExists;
        }

        @Override
        public TenantDomain save(TenantDomain tenant) {
            saved = tenant;
            return TenantDomain.rehydrate(
                    TENANT_ID,
                    tenant.getName(),
                    tenant.getTenantCode(),
                    tenant.getCreatedAt(),
                    tenant.getUpdatedAt()
            );
        }
    }

    private static final class FakeMembershipRepositoryPort
            implements MembershipRepositoryPort {
        private MembershipDomain saved;

        @Override
        public MembershipDomain save(MembershipDomain membership) {
            saved = membership;
            return MembershipDomain.rehydrate(
                    MEMBERSHIP_ID,
                    membership.getUserId(),
                    membership.getTenantId(),
                    membership.getRole(),
                    membership.getStatus(),
                    membership.getCreatedAt(),
                    membership.getUpdatedAt()
            );
        }

        @Override
        public Optional<MembershipDomain> findByUserIdAndTenantId(
                UUID userId,
                UUID tenantId
        ) {
            return Optional.empty();
        }
    }
}
