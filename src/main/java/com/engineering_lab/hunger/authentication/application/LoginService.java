package com.engineering_lab.hunger.authentication.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.authentication.api.LoginUseCase;
import com.engineering_lab.hunger.authentication.application.command.LoginCommand;
import com.engineering_lab.hunger.authentication.application.exception.InvalidCredentialsException;
import com.engineering_lab.hunger.authentication.application.result.GeneratedRefreshToken;
import com.engineering_lab.hunger.authentication.application.result.IssuedAccessToken;
import com.engineering_lab.hunger.authentication.application.result.AuthenticationTokens;
import com.engineering_lab.hunger.authentication.application.security.AccessTokenIssuer;
import com.engineering_lab.hunger.authentication.application.security.AuthenticationPolicy;
import com.engineering_lab.hunger.authentication.application.security.RefreshTokenGenerator;
import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.domain.repository.UserRepository;
import com.engineering_lab.hunger.user.domain.security.PasswordHasher;
import com.engineering_lab.hunger.user_session.domain.model.UserSessionDomain;
import com.engineering_lab.hunger.user_session.domain.repository.UserSessionRepository;

@Service
public class LoginService implements LoginUseCase {

    private static final String INVALID_EMAIL = "invalid-login@example.invalid";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final AccessTokenIssuer accessTokenIssuer;
    private final AuthenticationPolicy authenticationPolicy;
    private final Clock clock;

    private final String dummyPasswordHash;

    public LoginService(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            PasswordHasher passwordHasher,
            RefreshTokenGenerator refreshTokenGenerator,
            AccessTokenIssuer accessTokenIssuer,
            AuthenticationPolicy authenticationPolicy,
            Clock clock) {
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

    @Override
    @Transactional
    public AuthenticationTokens login(LoginCommand command) {
        UserDomain user = authenticate(command);
        Instant issuedAt = clock.instant();
        GeneratedRefreshToken refreshToken = refreshTokenGenerator.generate();
        Instant refreshTokenExpiresAt = issuedAt.plus(
                authenticationPolicy.refreshTokenTtl());

        UserSessionDomain session = UserSessionDomain.create(
                user.getId(),
                refreshToken.hash(),
                issuedAt,
                refreshTokenExpiresAt);

        UserSessionDomain savedSession = userSessionRepository.save(session);

        IssuedAccessToken accessToken = accessTokenIssuer.issue(
                user.getId(),
                savedSession.getId());

        return new AuthenticationTokens(
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.value(),
                refreshTokenExpiresAt);
    }

    private UserDomain authenticate(LoginCommand command) {
        String normalizedEmail;
        boolean validEmail = true;

        try {
            normalizedEmail = Validator.normalizeEmail(
                    command.email());
        } catch (IllegalArgumentException exception) {
            normalizedEmail = INVALID_EMAIL;
            validEmail = false;
        }

        Optional<UserDomain> candidate = userRepository.findByNormalizedEmail(
                normalizedEmail);

        String passwordHash = candidate
                .map(UserDomain::getPasswordHash)
                .orElse(dummyPasswordHash);

        boolean passwordMatches = passwordHasher.matches(
                command.password(),
                passwordHash);

        if (!validEmail
                || candidate.isEmpty()
                || !passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return candidate.get();
    }
}
