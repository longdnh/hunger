package com.engineering_lab.hunger.invitation.web.dto;

import java.util.UUID;

import com.engineering_lab.hunger.invitation.application.result.AcceptedInvitationResult;
import com.engineering_lab.hunger.membership.domain.model.MembershipRole;
import com.engineering_lab.hunger.user.web.dto.UserResponseDto;

public record AcceptedInvitationResponseDto(
        UserResponseDto user, UUID tenantId, MembershipRole role
) {
    public static AcceptedInvitationResponseDto from(AcceptedInvitationResult result) {
        return new AcceptedInvitationResponseDto(
                UserResponseDto.from(result.user()), result.tenantId(), result.role());
    }
}
