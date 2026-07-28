package com.engineering_lab.hunger.authentication.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.authentication.application.exception.InvalidCredentialsException;
import com.engineering_lab.hunger.authentication.application.exception.InvalidRefreshTokenException;
import com.engineering_lab.hunger.authentication.application.port.AccessTokenIssuerPort;
import com.engineering_lab.hunger.authentication.application.port.AuthenticationPolicyPort;
import com.engineering_lab.hunger.authentication.application.port.RefreshTokenGeneratorPort;
import com.engineering_lab.hunger.authentication.application.result.AuthenticationResult;
import com.engineering_lab.hunger.authentication.application.result.GeneratedRefreshTokenResult;
import com.engineering_lab.hunger.authentication.application.result.IssuedAccessTokenResult;
import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.domain.repository.UserRepository;
import com.engineering_lab.hunger.user.domain.security.PasswordHasher;
import com.engineering_lab.hunger.user_session.domain.model.UserSessionDomain;
import com.engineering_lab.hunger.user_session.domain.repository.UserSessionRepository;

@Service
public class AuthenticationService {

    private static final String INVALID_EMAIL =
            "invalid-login@example.invalid";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenGeneratorPort refreshTokenGenerator;
    private final AccessTokenIssuerPort accessTokenIssuer;
    private final AuthenticationPolicyPort authenticationPolicy;
    private final Clock clock;
    private final String dummyPasswordHash;

    public AuthenticationService(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            PasswordHasher passwordHasher,
            RefreshTokenGeneratorPort refreshTokenGenerator,
            AccessTokenIssuerPort accessTokenIssuer,
            AuthenticationPolicyPort authenticationPolicy,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordHasher = passwordHasher;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.accessTokenIssuer = accessTokenIssuer;
        this.authenticationPolicy = authenticationPolicy;
        this.clock = clock;
        this.dummyPasswordHash = passwordHasher.hash(
                UUID.randomUUID().toString());
    }

    @Transactional
    public AuthenticationResult login(
            String email,
            String password
    ) {
        UserDomain user = authenticate(email, password);
        Instant issuedAt = clock.instant();

        return issueTokens(
                user.getId(),
                issuedAt);
    }

    @Transactional
    public AuthenticationResult refresh(
            String rawRefreshToken
    ) {
        if (rawRefreshToken == null
                || rawRefreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        String tokenHash = refreshTokenGenerator.hash(
                rawRefreshToken);

        UserSessionDomain currentSession =
                userSessionRepository
                        .findByTokenHashForUpdate(tokenHash)
                        .orElseThrow(
                                InvalidRefreshTokenException::new);

        Instant now = clock.instant();

        if (!currentSession.isActive(now)) {
            throw new InvalidRefreshTokenException();
        }

        currentSession.revoke(now);
        userSessionRepository.save(currentSession);

        return issueTokens(
                currentSession.getUserId(),
                now);
    }

    private UserDomain authenticate(
            String email,
            String password
    ) {
        String normalizedEmail;
        boolean validEmail = true;

        try {
            normalizedEmail = Validator.normalizeEmail(email);
        } catch (IllegalArgumentException exception) {
            normalizedEmail = INVALID_EMAIL;
            validEmail = false;
        }

        Optional<UserDomain> candidate =
                userRepository.findByNormalizedEmail(
                        normalizedEmail);

        String passwordHash = candidate
                .map(UserDomain::getPasswordHash)
                .orElse(dummyPasswordHash);

        boolean validPassword =
                password != null && !password.isBlank();

        boolean passwordMatches = passwordHasher.matches(
                password == null ? "" : password,
                passwordHash);

        if (!validEmail
                || !validPassword
                || candidate.isEmpty()
                || !passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return candidate.get();
    }

    private AuthenticationResult issueTokens(
            UUID userId,
            Instant issuedAt
    ) {
        GeneratedRefreshTokenResult refreshToken =
                refreshTokenGenerator.generate();

        Instant refreshTokenExpiresAt = issuedAt.plus(
                authenticationPolicy.refreshTokenTtl());

        UserSessionDomain session = UserSessionDomain.create(
                userId,
                refreshToken.hash(),
                issuedAt,
                refreshTokenExpiresAt);

        UserSessionDomain savedSession =
                userSessionRepository.save(session);

        IssuedAccessTokenResult accessToken =
                accessTokenIssuer.issue(
                        userId,
                        savedSession.getId());

        return new AuthenticationResult(
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.value(),
                refreshTokenExpiresAt);
    }
}
