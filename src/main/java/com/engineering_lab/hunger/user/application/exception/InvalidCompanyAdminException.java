package com.engineering_lab.hunger.user.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class InvalidCompanyAdminException extends AppException {

    public InvalidCompanyAdminException(String message) {
        super(HttpStatus.CONFLICT, "INVALID_COMPANY_ADMIN", message);
    }
}
