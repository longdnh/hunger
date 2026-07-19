package com.engineering_lab.hunger.tenant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.membership.domain.model.MembershipRole;
import com.engineering_lab.hunger.membership.domain.repository.MembershipRepository;
import com.engineering_lab.hunger.tenant.application.command.CreateTenantCommand;
import com.engineering_lab.hunger.tenant.application.result.CreatedTenant;
import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;
import com.engineering_lab.hunger.tenant.domain.repository.TenantRepository;

class CreateTenantServiceTest {
    private static final UUID USER_ID = UUID.fromString(
            "01890f9a-6b7c-7def-8123-456789abcdef"
    );
    private static final UUID TENANT_ID = UUID.fromString(
            "01890f9a-6b7c-7def-9234-56789abcdef0"
    );
    private static final UUID MEMBERSHIP_ID = UUID.fromString(
            "01890f9a-6b7c-7def-a345-6789abcdef01"
    );
    private static final Instant NOW = Instant.parse("2026-07-19T08:00:00Z");

    @Test
    void createsTenantAndOwnerMembershipInTheSameUseCase() {
        FakeTenantRepository tenantRepository = new FakeTenantRepository();
        FakeMembershipRepository membershipRepository = new FakeMembershipRepository();
        CreateTenantService service = new CreateTenantService(
                tenantRepository,
                membershipRepository,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );

        CreatedTenant result = service.execute(new CreateTenantCommand(
                USER_ID,
                "  Engineering Lab  ",
                " hn_01 "
        ));

        assertEquals(TENANT_ID, result.tenantId());
        assertEquals("Engineering Lab", result.name());
        assertEquals("HN_01", result.tenantCode());
        assertEquals(NOW, result.createdAt());
        assertEquals(USER_ID, membershipRepository.saved.getUserId());
        assertEquals(TENANT_ID, membershipRepository.saved.getTenantId());
        assertEquals(MembershipRole.OWNER, membershipRepository.saved.getRole());
    }

    @Test
    void rejectsAnExistingNormalizedTenantCodeBeforeWriting() {
        FakeTenantRepository tenantRepository = new FakeTenantRepository();
        tenantRepository.codeExists = true;
        FakeMembershipRepository membershipRepository = new FakeMembershipRepository();
        CreateTenantService service = new CreateTenantService(
                tenantRepository,
                membershipRepository,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );

        assertThrows(
                TenantCodeAlreadyExistsException.class,
                () -> service.execute(new CreateTenantCommand(USER_ID, "Hunger", "hn_01"))
        );
        assertEquals("HN_01", tenantRepository.checkedCode);
        assertNull(tenantRepository.saved);
        assertNull(membershipRepository.saved);
    }

    private static final class FakeTenantRepository implements TenantRepository {
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

    private static final class FakeMembershipRepository implements MembershipRepository {
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
    }
}
