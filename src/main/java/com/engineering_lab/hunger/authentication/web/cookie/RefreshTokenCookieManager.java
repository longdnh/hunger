package com.engineering_lab.hunger.authentication.web.cookie;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.authentication.infra.config.RefreshTokenCookieProperties;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RefreshTokenCookieManager {

    private final RefreshTokenCookieProperties properties;
    private final Clock clock;

    public RefreshTokenCookieManager(
            RefreshTokenCookieProperties properties,
            Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public ResponseCookie create(
            String refreshToken,
            Instant expiresAt) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException(
                    "refreshToken must not be blank");
        }

        Objects.requireNonNull(
                expiresAt,
                "expiresAt must not be null");

        Duration maxAge = Duration.between(
                clock.instant(),
                expiresAt);

        if (maxAge.isNegative() || maxAge.isZero()) {
            throw new IllegalArgumentException(
                    "refresh token must not be expired");
        }

        return ResponseCookie
                .from(properties.name(), refreshToken)
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite(properties.sameSite())
                .path(properties.path())
                .maxAge(maxAge)
                .build();
    }

    public Optional<String> read(
            HttpServletRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request must not be null");

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (properties.name().equals(cookie.getName())
                    && cookie.getValue() != null
                    && !cookie.getValue().isBlank()) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    public ResponseCookie delete() {
        return ResponseCookie
                .from(properties.name(), "")
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite(properties.sameSite())
                .path(properties.path())
                .maxAge(Duration.ZERO)
                .build();
    }
}
