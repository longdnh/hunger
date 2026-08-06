package com.engineering_lab.hunger.user.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterInvitedUserRequestDto(
        @NotBlank String invitationToken,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(min = 12, max = 72) String password
) {
    @Override
    public String toString() {
        return "RegisterInvitedUserRequestDto[invitationToken=[REDACTED], name="
                + name + ", password=[REDACTED]]";
    }
}
