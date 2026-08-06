package com.engineering_lab.hunger.user.domain.exception;

public final class UserErrorCode {

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String INVALID_REGISTRATION = "INVALID_REGISTRATION";

    private UserErrorCode() {
        throw new AssertionError(
                "Utility class must not be instantiated");
    }
}
