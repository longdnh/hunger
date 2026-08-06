package com.engineering_lab.hunger.tenant.application.exception;

import org.springframework.http.HttpStatus;

import com.engineering_lab.hunger.common.exception.AppException;

public final class TopAdminRequiredException extends AppException {

    public TopAdminRequiredException() {
        super(HttpStatus.FORBIDDEN, "TOP_ADMIN_REQUIRED", "Top-admin permission is required");
    }
}
