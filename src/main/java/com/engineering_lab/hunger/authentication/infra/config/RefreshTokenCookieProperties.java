package com.engineering_lab.hunger.authentication.infra.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "security.authentication.refresh-cookie")
public record RefreshTokenCookieProperties(

        @NotBlank String name,

        @NotBlank String path,

        @NotBlank String sameSite,

        boolean secure) {

    private static final Set<String> ALLOWED_SAME_SITE = Set.of("Strict", "Lax", "None");

    public RefreshTokenCookieProperties {
        if (!ALLOWED_SAME_SITE.contains(sameSite)) {
            throw new IllegalArgumentException(
                    "sameSite must be Strict, Lax or None");
        }

        if ("None".equals(sameSite) && !secure) {
            throw new IllegalArgumentException(
                    "SameSite=None requires Secure=true");
        }
    }
}
