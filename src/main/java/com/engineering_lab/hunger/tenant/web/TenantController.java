package com.engineering_lab.hunger.tenant.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.engineering_lab.hunger.common.security.AuthenticatedUserIdResolver;
import com.engineering_lab.hunger.tenant.api.CreateTenantUseCase;
import com.engineering_lab.hunger.tenant.application.command.CreateTenantCommand;
import com.engineering_lab.hunger.tenant.application.result.CreatedTenant;
import com.engineering_lab.hunger.tenant.web.dto.CreateTenantRequest;
import com.engineering_lab.hunger.tenant.web.dto.CreateTenantResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {
    private final CreateTenantUseCase createTenantUseCase;
    private final AuthenticatedUserIdResolver authenticatedUserIdResolver;

    public TenantController(
            CreateTenantUseCase createTenantUseCase,
            AuthenticatedUserIdResolver authenticatedUserIdResolver
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.authenticatedUserIdResolver = authenticatedUserIdResolver;
    }

    @PostMapping
    public ResponseEntity<CreateTenantResponse> createTenant(
            @Valid @RequestBody CreateTenantRequest request,
            Authentication authentication
    ) {
        UUID creatorUserId = authenticatedUserIdResolver.resolve(authentication);
        CreatedTenant tenant = createTenantUseCase.execute(new CreateTenantCommand(
                creatorUserId,
                request.name(),
                request.tenantCode()
        ));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{tenantId}")
                .buildAndExpand(tenant.tenantId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CreateTenantResponse.from(tenant));
    }
}
