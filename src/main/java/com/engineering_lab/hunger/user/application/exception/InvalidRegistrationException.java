package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class InvalidRegistrationException
        extends AppException {

    public InvalidRegistrationException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                "INVALID_REGISTRATION",
                message);
    }
}
