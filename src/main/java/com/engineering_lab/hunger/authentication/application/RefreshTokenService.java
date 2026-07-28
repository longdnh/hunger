package com.engineering_lab.hunger.authentication.application;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.authentication.application.exception.InvalidRefreshTokenException;
import com.engineering_lab.hunger.authentication.application.result.AuthenticationTokens;
import com.engineering_lab.hunger.authentication.application.result.GeneratedRefreshToken;
import com.engineering_lab.hunger.authentication.application.result.IssuedAccessToken;
import com.engineering_lab.hunger.authentication.application.security.AccessTokenIssuer;
import com.engineering_lab.hunger.authentication.application.security.AuthenticationPolicy;
import com.engineering_lab.hunger.authentication.application.security.RefreshTokenGenerator;
import com.engineering_lab.hunger.user_session.domain.model.UserSessionDomain;
import com.engineering_lab.hunger.user_session.domain.repository.UserSessionRepository;

@Service
public class RefreshTokenService {

    private final UserSessionRepository userSessionRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final AccessTokenIssuer accessTokenIssuer;
    private final AuthenticationPolicy authenticationPolicy;
    private final Clock clock;

    public RefreshTokenService(
            UserSessionRepository userSessionRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            AccessTokenIssuer accessTokenIssuer,
            AuthenticationPolicy authenticationPolicy,
            Clock clock) {
        this.userSessionRepository = userSessionRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.accessTokenIssuer = accessTokenIssuer;
        this.authenticationPolicy = authenticationPolicy;
        this.clock = clock;
    }

    @Transactional
    public AuthenticationTokens refresh(String rawRefreshToken) {
        if (rawRefreshToken == null
                || rawRefreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        String tokenHash = refreshTokenGenerator.hash(
                rawRefreshToken);

        UserSessionDomain currentSession = userSessionRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(
                        InvalidRefreshTokenException::new);

        Instant now = clock.instant();

        if (!currentSession.isActive(now)) {
            throw new InvalidRefreshTokenException();
        }

        currentSession.revoke(now);
        userSessionRepository.save(currentSession);

        GeneratedRefreshToken newRefreshToken = refreshTokenGenerator.generate();

        Instant newRefreshTokenExpiresAt = now.plus(
                authenticationPolicy.refreshTokenTtl());

        UserSessionDomain newSession = UserSessionDomain.create(
                currentSession.getUserId(),
                newRefreshToken.hash(),
                now,
                newRefreshTokenExpiresAt);

        UserSessionDomain savedNewSession = userSessionRepository.save(newSession);

        IssuedAccessToken newAccessToken = accessTokenIssuer.issue(
                currentSession.getUserId(),
                savedNewSession.getId());

        return new AuthenticationTokens(
                newAccessToken.token(),
                newAccessToken.expiresAt(),
                newRefreshToken.value(),
                newRefreshTokenExpiresAt);
    }
}
