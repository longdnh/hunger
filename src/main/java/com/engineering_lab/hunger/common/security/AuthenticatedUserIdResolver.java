package com.engineering_lab.hunger.common.security;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.common.exception.AppException;
import com.engineering_lab.hunger.common.validator.Validator;

@Component
public class AuthenticatedUserIdResolver {
    public UUID resolve(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw authenticationRequired();
        }

        try {
            return Validator.requireUuidV7(
                    UUID.fromString(authentication.getName()),
                    "authenticatedUserId"
            );
        } catch (IllegalArgumentException exception) {
            throw authenticationRequired();
        }
    }

    private static AppException authenticationRequired() {
        return new AppException(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_REQUIRED",
                "Authentication is required"
        );
    }
}
