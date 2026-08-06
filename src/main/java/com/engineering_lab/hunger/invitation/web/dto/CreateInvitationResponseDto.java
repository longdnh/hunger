package com.engineering_lab.hunger.invitation.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.invitation.application.result.CreatedInvitationResult;
import com.engineering_lab.hunger.membership.domain.model.MembershipRole;

public record CreateInvitationResponseDto(
        UUID invitationId, UUID tenantId, String email, MembershipRole role,
        String invitationToken, Instant expiresAt
) {
    public static CreateInvitationResponseDto from(CreatedInvitationResult result) {
        return new CreateInvitationResponseDto(
                result.invitationId(), result.tenantId(), result.email(), result.role(),
                result.invitationToken(), result.expiresAt());
    }
    @Override
    public String toString() {
        return "CreateInvitationResponseDto[invitationId=" + invitationId
                + ", tenantId=" + tenantId + ", email=[REDACTED], role=" + role
                + ", invitationToken=[REDACTED], expiresAt=" + expiresAt + "]";
    }
}
