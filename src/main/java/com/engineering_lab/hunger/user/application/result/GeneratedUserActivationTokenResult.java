package com.engineering_lab.hunger.user.application.result;

public record GeneratedUserActivationTokenResult(
        String value,
        String hash
) {

    @Override
    public String toString() {
        return "GeneratedUserActivationTokenResult[value=[REDACTED], hash=[REDACTED]]";
    }
}
