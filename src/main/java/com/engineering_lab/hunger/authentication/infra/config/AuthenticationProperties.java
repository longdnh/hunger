package com.engineering_lab.hunger.authentication.infra.config;

import java.time.Duration;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.engineering_lab.hunger.authentication.application.port.AuthenticationPolicyPort;

import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "security.authentication")
public record AuthenticationProperties(
        @NotNull Duration refreshTokenTtl
) implements AuthenticationPolicyPort {

    public AuthenticationProperties {
        Objects.requireNonNull(
                refreshTokenTtl,
                "refreshTokenTtl must not be null");

        if (refreshTokenTtl.isZero()
                || refreshTokenTtl.isNegative()) {
            throw new IllegalArgumentException(
                    "refreshTokenTtl must be positive");
        }
    }
}
