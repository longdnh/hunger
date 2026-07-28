package com.engineering_lab.hunger.authentication.application.result;

public final class GeneratedRefreshTokenResult {

    private final String value;
    private final String hash;

    public GeneratedRefreshTokenResult(
            String value,
            String hash
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "value must not be blank");
        }

        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException(
                    "hash must not be blank");
        }

        this.value = value;
        this.hash = hash;
    }

    public String value() {
        return value;
    }

    public String hash() {
        return hash;
    }
}
