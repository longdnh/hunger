package com.engineering_lab.hunger.invitation.application.result;

import java.util.UUID;

import com.engineering_lab.hunger.membership.domain.model.MembershipRole;
import com.engineering_lab.hunger.user.application.result.UserResult;

public record AcceptedInvitationResult(
        UserResult user,
        UUID tenantId,
        MembershipRole role
) {
}
