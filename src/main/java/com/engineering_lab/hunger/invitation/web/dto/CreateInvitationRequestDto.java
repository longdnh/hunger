package com.engineering_lab.hunger.invitation.web.dto;

import com.engineering_lab.hunger.membership.domain.model.MembershipRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateInvitationRequestDto(
        @NotBlank @Email @Size(max = 100) String email,
        @NotNull MembershipRole role
) {
}
