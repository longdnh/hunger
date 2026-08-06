package com.engineering_lab.hunger.invitation.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.engineering_lab.hunger.common.security.AuthenticatedUserIdResolver;
import com.engineering_lab.hunger.invitation.application.InvitationService;
import com.engineering_lab.hunger.invitation.web.dto.AcceptInvitationRequestDto;
import com.engineering_lab.hunger.invitation.web.dto.AcceptedInvitationResponseDto;
import com.engineering_lab.hunger.invitation.web.dto.CreateInvitationRequestDto;
import com.engineering_lab.hunger.invitation.web.dto.CreateInvitationResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class InvitationController {
    private final InvitationService invitationService;
    private final AuthenticatedUserIdResolver userIdResolver;

    public InvitationController(
            InvitationService invitationService,
            AuthenticatedUserIdResolver userIdResolver) {
        this.invitationService = invitationService;
        this.userIdResolver = userIdResolver;
    }

    @PostMapping("/tenants/{tenantId}/invitations")
    public ResponseEntity<CreateInvitationResponseDto> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateInvitationRequestDto request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CreateInvitationResponseDto.from(invitationService.create(
                        userIdResolver.resolve(authentication), tenantId,
                        request.email(), request.role())));
    }

    @PostMapping("/invitations/accept")
    public AcceptedInvitationResponseDto accept(
            @Valid @RequestBody AcceptInvitationRequestDto request,
            Authentication authentication) {
        return AcceptedInvitationResponseDto.from(invitationService.accept(
                userIdResolver.resolve(authentication), request.invitationToken()));
    }
}
