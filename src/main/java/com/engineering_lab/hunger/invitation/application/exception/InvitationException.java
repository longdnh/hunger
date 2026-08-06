package com.engineering_lab.hunger.invitation.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class InvitationException extends AppException {

    private InvitationException(HttpStatus status, String code, String message) {
        super(status, code, message);
    }

    public static InvitationException invalid() {
        return new InvitationException(
                HttpStatus.BAD_REQUEST, "INVALID_INVITATION", "Invitation is invalid or expired");
    }

    public static InvitationException adminRequired() {
        return new InvitationException(
                HttpStatus.FORBIDDEN, "COMPANY_ADMIN_REQUIRED", "Company-admin permission is required");
    }

    public static InvitationException accountExists() {
        return new InvitationException(
                HttpStatus.CONFLICT, "ACCOUNT_ALREADY_EXISTS", "Account already exists; sign in and accept the invitation");
    }

    public static InvitationException membershipExists() {
        return new InvitationException(
                HttpStatus.CONFLICT, "MEMBERSHIP_ALREADY_EXISTS", "User already belongs to this tenant");
    }
}
