package com.engineering_lab.hunger.user.web;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.engineering_lab.hunger.common.security.AuthenticatedUserIdResolver;
import com.engineering_lab.hunger.user.application.UserService;
import com.engineering_lab.hunger.user.web.dto.UserResponseDto;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

        private final UserService userService;
        private final AuthenticatedUserIdResolver authenticatedUserIdResolver;

        public UserController(
                        UserService userService,
                        AuthenticatedUserIdResolver authenticatedUserIdResolver) {
                this.userService = userService;
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
}
