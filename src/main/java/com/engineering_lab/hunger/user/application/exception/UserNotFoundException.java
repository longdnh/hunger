package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;
import com.engineering_lab.hunger.user.domain.exception.UserErrorCode;

public final class UserNotFoundException
        extends AppException {

    public UserNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                UserErrorCode.USER_NOT_FOUND,
                "User was not found");
    }
}
