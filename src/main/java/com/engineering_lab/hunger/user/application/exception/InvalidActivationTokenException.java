package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class InvalidActivationTokenException extends AppException {

    public InvalidActivationTokenException() {
        super(HttpStatus.UNAUTHORIZED, "INVALID_ACTIVATION_TOKEN", "Activation token is invalid or expired");
    }
}
