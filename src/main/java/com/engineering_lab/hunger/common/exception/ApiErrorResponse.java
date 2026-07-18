package com.engineering_lab.hunger.common.exception;

public record ApiErrorResponse(
        int status,
        String code,
        String message,
        String path) {

}
