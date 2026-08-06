package com.engineering_lab.hunger.tenant.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import com.engineering_lab.hunger.tenant.application.TenantService;
import com.engineering_lab.hunger.tenant.application.result.CreateTenantResult;

class TenantControllerTest {
    private static final UUID USER_ID = UUID.fromString(
            "01890f9a-6b7c-7def-8123-456789abcdef"
    );
    private static final UUID TENANT_ID = UUID.fromString(
            "01890f9a-6b7c-7def-9234-56789abcdef0"
    );
    private static final Instant CREATED_AT = Instant.parse("2026-07-19T08:00:00Z");

    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        TenantService tenantService = mock(TenantService.class);
        when(tenantService.create(any(), any(), any(), any(), any()))
                .thenReturn(new CreateTenantResult(
                        TENANT_ID, "Engineering Lab", "HN_01", CREATED_AT,
                        USER_ID, "admin@example.com", "activation-token",
                        CREATED_AT.plusSeconds(3600)));

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

        mockMvc.perform(post("/api/v1/platform/tenants")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Engineering Lab",
                                  "tenantCode": "hn_01",
                                  "companyAdminName": "Company Admin",
                                  "companyAdminEmail": "admin@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        HttpHeaders.LOCATION,
                        "http://localhost/api/v1/platform/tenants/" + TENANT_ID
                ))
                .andExpect(jsonPath("$.tenantId").value(TENANT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Engineering Lab"))
                .andExpect(jsonPath("$.tenantCode").value("HN_01"));

    }

    @Test
    void rejectsInvalidRequestBody() throws Exception {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(USER_ID.toString(), null, List.of());

        mockMvc.perform(post("/api/v1/platform/tenants")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "tenantCode": "TOO_LONG",
                                 "companyAdminName": "", "companyAdminEmail": "invalid"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void requiresAnAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/v1/platform/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Engineering Lab", "tenantCode": "HN_01",
                                 "companyAdminName": "Company Admin",
                                 "companyAdminEmail": "admin@example.com"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
    }

}
