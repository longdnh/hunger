package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class EmailAlreadyExistsException
        extends AppException {

    public EmailAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_EXISTS",
                "Email is already in use");
    }
}
