package com.engineering_lab.hunger.common.exception;

public record ApiErrorResponseDto(
        int status,
        String code,
        String message,
        String path) {

}
