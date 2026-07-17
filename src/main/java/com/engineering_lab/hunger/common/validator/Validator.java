package com.engineering_lab.hunger.common.validator;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Validator {
    private static final int EMAIL_MAX_LENGTH = 254;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9!#$%&'*+/=?^_`{|}~.-]+@"
                    + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                    + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$"
    );
    private static final Pattern TENANT_CODE_PATTERN = Pattern.compile("^[A-Z0-9_-]{1,5}$");

    private Validator() {
        throw new AssertionError("Utility class must not be instantiated");
    }

    public static String requireText(String value, String fieldName, int maxLength) {
        requireFieldName(fieldName);
        if (maxLength < 1) {
            throw new IllegalArgumentException("maxLength must be greater than zero");
        }

        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        if (trimmedValue.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters");
        }
        return trimmedValue;
    }

    public static String requireEmail(String email) {
        String validatedEmail = requireText(email, "email", EMAIL_MAX_LENGTH);
        int atIndex = validatedEmail.lastIndexOf('@');
        String localPart = atIndex < 0 ? "" : validatedEmail.substring(0, atIndex);

        if (localPart.startsWith(".")
                || localPart.endsWith(".")
                || localPart.contains("..")
                || !EMAIL_PATTERN.matcher(validatedEmail).matches()) {
            throw new IllegalArgumentException("email is not valid");
        }
        return validatedEmail;
    }

    public static String normalizeEmail(String email) {
        return requireEmail(email).toLowerCase(Locale.ROOT);
    }

    public static UUID requireUuidV7(UUID uuid, String fieldName) {
        requireFieldName(fieldName);
        if (uuid == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (uuid.version() != 7) {
            throw new IllegalArgumentException(fieldName + " must be a UUIDv7");
        }
        return uuid;
    }

    public static Instant requireNotBefore(Instant value, Instant reference, String fieldName) {
        requireFieldName(fieldName);
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        Objects.requireNonNull(reference, "reference must not be null");
        if (value.isBefore(reference)) {
            throw new IllegalArgumentException(fieldName + " must not be before " + reference);
        }
        return value;
    }

    public static String normalizeTenantCode(String value) {
        String tenantCode = requireText(value, "tenantCode", 5).toUpperCase(Locale.ROOT);
        if (!TENANT_CODE_PATTERN.matcher(tenantCode).matches()) {
            throw new IllegalArgumentException(
                    "tenantCode must contain only letters, numbers, underscores, or hyphens"
            );
        }
        return tenantCode;
    }

    private static void requireFieldName(String fieldName) {
        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("fieldName must not be blank");
        }
    }
}
