package com.engineering_lab.hunger.invitation.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AcceptInvitationRequestDto(@NotBlank String invitationToken) {
    @Override
    public String toString() {
        return "AcceptInvitationRequestDto[invitationToken=[REDACTED]]";
    }
}
