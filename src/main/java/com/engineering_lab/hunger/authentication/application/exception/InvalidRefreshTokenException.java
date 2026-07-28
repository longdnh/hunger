package com.engineering_lab.hunger.authentication.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class InvalidRefreshTokenException extends AppException {

    public InvalidRefreshTokenException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "INVALID_REFRESH_TOKEN",
                "Refresh token is invalid or expired");
    }
}
