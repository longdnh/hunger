package com.engineering_lab.hunger.user.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.bootstrap.top-admin")
public record TopAdminBootstrapProperties(
        boolean enabled,
        String name,
        String email,
        String password) {

    @Override
    public String toString() {
        return """
                TopAdminBootstrapProperties[
                    enabled=%s,
                    name=%s,
                    email=[REDACTED],
                    password=[REDACTED]
                ]
                """.formatted(enabled, name);
    }
}
