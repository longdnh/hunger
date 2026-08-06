package com.engineering_lab.hunger.user.infra.config;

import java.time.Duration;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.engineering_lab.hunger.user.application.port.UserPolicyPort;

@ConfigurationProperties(prefix = "security.user")
public record UserProperties(
        Duration activationTokenTtl
) implements UserPolicyPort {

    public UserProperties {
        Objects.requireNonNull(activationTokenTtl, "activationTokenTtl must not be null");
        if (activationTokenTtl.isZero() || activationTokenTtl.isNegative()) {
            throw new IllegalArgumentException("activationTokenTtl must be positive");
        }
    }
}
