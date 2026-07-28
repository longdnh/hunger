package com.engineering_lab.hunger.authentication.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class InvalidCredentialsException
        extends AppException {

    public InvalidCredentialsException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Email or password is incorrect");
    }
}
