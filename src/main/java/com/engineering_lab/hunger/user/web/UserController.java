package com.engineering_lab.hunger.user.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.engineering_lab.hunger.common.security.AuthenticatedUserIdResolver;
import com.engineering_lab.hunger.user.application.UserService;
import com.engineering_lab.hunger.user.web.dto.UserResponseDto;
import com.engineering_lab.hunger.user.web.dto.RegisterInvitedUserRequestDto;
import com.engineering_lab.hunger.invitation.application.InvitationService;
import com.engineering_lab.hunger.invitation.web.dto.AcceptedInvitationResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

        private final UserService userService;
        private final InvitationService invitationService;
        private final AuthenticatedUserIdResolver authenticatedUserIdResolver;

        public UserController(
                        UserService userService,
                        InvitationService invitationService,
                        AuthenticatedUserIdResolver authenticatedUserIdResolver) {
                this.userService = userService;
                this.invitationService = invitationService;
                this.authenticatedUserIdResolver = authenticatedUserIdResolver;
        }

        @GetMapping("/me")
        public UserResponseDto currentUser(
                        Authentication authentication) {
                UUID userId = authenticatedUserIdResolver.resolve(
                                authentication);

                return UserResponseDto.from(
                                userService.currentUser(userId));
        }

        @PostMapping("/register")
        public ResponseEntity<AcceptedInvitationResponseDto> register(
                        @Valid @RequestBody RegisterInvitedUserRequestDto request) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                AcceptedInvitationResponseDto.from(
                                                invitationService.register(
                                                                request.invitationToken(),
                                                                request.name(),
                                                                request.password())));
        }
}
