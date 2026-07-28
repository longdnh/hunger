package com.engineering_lab.hunger.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

import com.engineering_lab.hunger.tenant.domain.exception.TenantCodeAlreadyExistsException;
import com.engineering_lab.hunger.tenant.domain.exception.TenantErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Override the default exception handling to provide custom error responses
    @Override
    @Nullable
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        ResponseEntity<Object> frameworkResponse = super.handleExceptionInternal(
                exception,
                body,
                headers,
                statusCode,
                request);

        if (frameworkResponse == null) {
            return null;
        }

        String path = requestPath(request);
        String code = statusCodeName(statusCode);

        if (statusCode.is5xxServerError()) {
            LOG.error(
                    "Spring MVC error: status={}, code={}, path={}",
                    statusCode.value(),
                    code,
                    path,
                    exception);
        } else {
            LOG.warn(
                    "Request rejected: status={}, code={}, path={}, exception={}",
                    statusCode.value(),
                    code,
                    path,
                    exception.getClass().getSimpleName());
        }

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                statusCode.value(),
                code,
                publicMessage(statusCode),
                path);

        return new ResponseEntity<>(
                response,
                frameworkResponse.getHeaders(),
                frameworkResponse.getStatusCode());
    }

    // Handle custom application exceptions
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponseDto> handleAppException(
            AppException exception,
            HttpServletRequest request) {

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                exception.getStatus().value(),
                exception.getCode(),
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(TenantCodeAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponseDto> handleTenantCodeAlreadyExists(
            TenantCodeAlreadyExistsException exception,
            HttpServletRequest request) {
        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                TenantErrorCode.TENANT_CODE_ALREADY_EXISTS,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // Handle any other unexpected exceptions that may occur
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        LOG.error(
                "Unexpected error: method={}, path={}",
                request.getMethod(),
                request.getRequestURI(),
                exception);

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private static String requestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return "";
    }

    private static String statusCodeName(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status != null
                ? status.name()
                : "HTTP_" + statusCode.value();
    }

    private static String publicMessage(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 400 -> "The request is invalid";
            case 404 -> "The requested resource was not found";
            case 405 -> "The HTTP method is not supported";
            case 406 -> "The requested response format is not supported";
            case 409 -> "The request conflicts with the current resource state";
            case 415 -> "The request media type is not supported";
            case 422 -> "The request could not be validated";
            case 429 -> "Too many requests";
            default -> statusCode.is5xxServerError()
                    ? "An unexpected error occurred"
                    : "The request could not be processed";
        };
    }

}
