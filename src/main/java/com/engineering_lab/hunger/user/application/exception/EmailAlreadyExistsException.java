package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;
import com.engineering_lab.hunger.user.domain.exception.UserErrorCode;

public final class EmailAlreadyExistsException
        extends AppException {

    public EmailAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT,
                UserErrorCode.EMAIL_ALREADY_EXISTS,
                "Email is already in use");
    }
}
