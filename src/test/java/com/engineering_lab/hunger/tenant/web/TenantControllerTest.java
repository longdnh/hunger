package com.engineering_lab.hunger.tenant.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.engineering_lab.hunger.common.exception.GlobalExceptionHandler;
import com.engineering_lab.hunger.common.security.AuthenticatedUserIdResolver;
import com.engineering_lab.hunger.membership.application.port.MembershipRepositoryPort;
import com.engineering_lab.hunger.membership.domain.model.MembershipDomain;
import com.engineering_lab.hunger.tenant.application.TenantService;
import com.engineering_lab.hunger.tenant.application.port.TenantRepositoryPort;
import com.engineering_lab.hunger.tenant.domain.model.TenantDomain;

class TenantControllerTest {
    private static final UUID USER_ID = UUID.fromString(
            "01890f9a-6b7c-7def-8123-456789abcdef"
    );
    private static final UUID TENANT_ID = UUID.fromString(
            "01890f9a-6b7c-7def-9234-56789abcdef0"
    );
    private static final Instant CREATED_AT = Instant.parse("2026-07-19T08:00:00Z");

    private MockMvc mockMvc;
    private CapturingMembershipRepositoryPort membershipRepository;

    @BeforeEach
    void setUp() {
        membershipRepository =
                new CapturingMembershipRepositoryPort();

        TenantService tenantService = new TenantService(
                new FakeTenantRepositoryPort(),
                membershipRepository,
                Clock.fixed(CREATED_AT, ZoneOffset.UTC));

        TenantController controller = new TenantController(
                tenantService,
                new AuthenticatedUserIdResolver()
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createsTenantForAuthenticatedUser() throws Exception {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(USER_ID.toString(), null, List.of());

        mockMvc.perform(post("/api/v1/tenants")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Engineering Lab",
                                  "tenantCode": "hn_01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        HttpHeaders.LOCATION,
                        "http://localhost/api/v1/tenants/" + TENANT_ID
                ))
                .andExpect(jsonPath("$.tenantId").value(TENANT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Engineering Lab"))
                .andExpect(jsonPath("$.tenantCode").value("HN_01"));

        org.junit.jupiter.api.Assertions.assertEquals(
                USER_ID,
                membershipRepository.saved.getUserId());
    }

    @Test
    void rejectsInvalidRequestBody() throws Exception {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(USER_ID.toString(), null, List.of());

        mockMvc.perform(post("/api/v1/tenants")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "tenantCode": "TOO_LONG"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void requiresAnAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Engineering Lab", "tenantCode": "HN_01"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
    }

    private static final class FakeTenantRepositoryPort
            implements TenantRepositoryPort {

        @Override
        public boolean existsByTenantCode(String tenantCode) {
            return false;
        }

        @Override
        public TenantDomain save(TenantDomain tenant) {
            return TenantDomain.rehydrate(
                    TENANT_ID,
                    tenant.getName(),
                    tenant.getTenantCode(),
                    tenant.getCreatedAt(),
                    tenant.getUpdatedAt());
        }
    }

    private static final class CapturingMembershipRepositoryPort
            implements MembershipRepositoryPort {

        private MembershipDomain saved;

        @Override
        public MembershipDomain save(
                MembershipDomain membership
        ) {
            saved = membership;
            return membership;
        }
    }
}
