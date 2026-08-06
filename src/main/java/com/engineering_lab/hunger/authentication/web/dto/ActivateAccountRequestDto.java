package com.engineering_lab.hunger.authentication.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivateAccountRequestDto(
        @NotBlank String activationToken,
        @NotBlank
        @Size(min = 12, max = 72)
        String password
) {

    @Override
    public String toString() {
        return "ActivateAccountRequestDto[activationToken=[REDACTED], password=[REDACTED]]";
    }
}
