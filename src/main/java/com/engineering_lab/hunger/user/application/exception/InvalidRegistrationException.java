package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;
import com.engineering_lab.hunger.user.domain.exception.UserErrorCode;

public final class InvalidRegistrationException
        extends AppException {

    public InvalidRegistrationException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                UserErrorCode.INVALID_REGISTRATION,
                message);
    }
}
