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
import com.engineering_lab.hunger.tenant.application.TenantService;
import com.engineering_lab.hunger.tenant.application.result.CreateTenantResult;
import com.engineering_lab.hunger.tenant.web.dto.CreateTenantRequestDto;
import com.engineering_lab.hunger.tenant.web.dto.CreateTenantResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;
    private final AuthenticatedUserIdResolver authenticatedUserIdResolver;

    public TenantController(
            TenantService tenantService,
            AuthenticatedUserIdResolver authenticatedUserIdResolver
    ) {
        this.tenantService = tenantService;
        this.authenticatedUserIdResolver = authenticatedUserIdResolver;
    }

    @PostMapping
    public ResponseEntity<CreateTenantResponseDto> createTenant(
            @Valid @RequestBody CreateTenantRequestDto request,
            Authentication authentication
    ) {
        UUID creatorUserId =
                authenticatedUserIdResolver.resolve(
                        authentication);

        CreateTenantResult tenant = tenantService.create(
                creatorUserId,
                request.name(),
                request.tenantCode());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{tenantId}")
                .buildAndExpand(tenant.tenantId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CreateTenantResponseDto.from(tenant));
    }
}
