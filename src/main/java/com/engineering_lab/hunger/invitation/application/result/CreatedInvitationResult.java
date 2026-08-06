package com.engineering_lab.hunger.invitation.application.result;

import java.time.Instant;
import java.util.UUID;

import com.engineering_lab.hunger.membership.domain.model.MembershipRole;

public record CreatedInvitationResult(
        UUID invitationId,
        UUID tenantId,
        String email,
        MembershipRole role,
        String invitationToken,
        Instant expiresAt
) {
    @Override
    public String toString() {
        return "CreatedInvitationResult[invitationId=" + invitationId
                + ", tenantId=" + tenantId + ", email=[REDACTED], role=" + role
                + ", invitationToken=[REDACTED], expiresAt=" + expiresAt + "]";
    }
}
