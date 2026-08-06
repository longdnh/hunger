package com.engineering_lab.hunger.user.application.result;

import java.time.Instant;

public record CompanyAdminProvisionResult(
        UserResult user,
        String activationToken,
        Instant activationExpiresAt
) {

    public boolean activationRequired() {
        return activationToken != null;
    }

    @Override
    public String toString() {
        return "CompanyAdminProvisionResult[user=" + user
                + ", activationToken=[REDACTED], activationExpiresAt="
                + activationExpiresAt + "]";
    }
}
