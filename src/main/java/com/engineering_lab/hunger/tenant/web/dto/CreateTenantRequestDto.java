package com.engineering_lab.hunger.tenant.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTenantRequestDto(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 5)
        @Pattern(regexp = "^[A-Za-z0-9_-]+$")
        String tenantCode
) {
}
