package com.engineering_lab.hunger.authentication.infra.security;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.authentication.application.port.AccessTokenIssuerPort;
import com.engineering_lab.hunger.authentication.application.result.IssuedAccessTokenResult;
import com.engineering_lab.hunger.common.security.jwt.JwtProperties;

@Component
public class JwtAccessTokenIssuerAdapter
        implements AccessTokenIssuerPort {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    public JwtAccessTokenIssuerAdapter(
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties,
            Clock clock
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
        this.clock = clock;
    }

    @Override
    public IssuedAccessTokenResult issue(
            UUID userId,
            UUID sessionId
    ) {
        Objects.requireNonNull(
                userId,
                "userId must not be null");

        Objects.requireNonNull(
                sessionId,
                "sessionId must not be null");

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(
                jwtProperties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .audience(List.of(jwtProperties.audience()))
                .subject(userId.toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("sid", sessionId.toString())
                .claim("jti", UUID.randomUUID().toString())
                .build();

        Jwt jwt = jwtEncoder.encode(
                JwtEncoderParameters.from(claims));

        return new IssuedAccessTokenResult(
                jwt.getTokenValue(),
                expiresAt);
    }
}
