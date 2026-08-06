package com.engineering_lab.hunger.user.application;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.user.application.exception.InvalidActivationTokenException;
import com.engineering_lab.hunger.user.application.exception.InvalidCompanyAdminException;
import com.engineering_lab.hunger.user.application.exception.InvalidRegistrationException;
import com.engineering_lab.hunger.user.application.exception.EmailAlreadyExistsException;
import com.engineering_lab.hunger.user.application.exception.UserNotFoundException;
import com.engineering_lab.hunger.user.application.port.PasswordHasherPort;
import com.engineering_lab.hunger.user.application.port.UserActivationRepositoryPort;
import com.engineering_lab.hunger.user.application.port.UserActivationTokenGeneratorPort;
import com.engineering_lab.hunger.user.application.port.UserPolicyPort;
import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.application.result.CompanyAdminProvisionResult;
import com.engineering_lab.hunger.user.application.result.GeneratedUserActivationTokenResult;
import com.engineering_lab.hunger.user.application.result.UserResult;
import com.engineering_lab.hunger.user.domain.model.UserActivationDomain;
import com.engineering_lab.hunger.user.domain.model.UserDomain;
import com.engineering_lab.hunger.user.domain.model.UserStatus;

@Service
public class UserService {

    private static final int PASSWORD_MIN_LENGTH = 12;
    private static final int PASSWORD_MAX_LENGTH = 72;

    private final UserRepositoryPort userRepository;
    private final UserActivationRepositoryPort activationRepository;
    private final PasswordHasherPort passwordHasher;
    private final UserActivationTokenGeneratorPort activationTokenGenerator;
    private final UserPolicyPort userPolicy;
    private final Clock clock;

    public UserService(
            UserRepositoryPort userRepository,
            UserActivationRepositoryPort activationRepository,
            PasswordHasherPort passwordHasher,
            UserActivationTokenGeneratorPort activationTokenGenerator,
            UserPolicyPort userPolicy,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.activationRepository = activationRepository;
        this.passwordHasher = passwordHasher;
        this.activationTokenGenerator = activationTokenGenerator;
        this.userPolicy = userPolicy;
        this.clock = clock;
    }

    @Transactional
    public UserResult bootstrapTopAdmin(
            String name,
            String email,
            String password
    ) {
        String validatedName;
        String validatedEmail;

        try {
            validatedName = Validator.requireText(
                    name,
                    "name",
                    100);
            validatedEmail = Validator.requireEmail(email);

            if (validatedEmail.length() > 100) {
                throw new IllegalArgumentException(
                        "email must not exceed 100 characters");
            }
        } catch (IllegalArgumentException exception) {
            throw new InvalidRegistrationException(
                    exception.getMessage());
        }

        validatePassword(password);

        String normalizedEmail = Validator.normalizeEmail(
                validatedEmail);

        Optional<UserDomain> existingUser =
                userRepository.findByNormalizedEmail(
                        normalizedEmail);

        if (existingUser.isPresent()) {
            UserDomain user = existingUser.get();
            user.grantTopAdmin(clock.instant());

            return UserResult.from(
                    userRepository.save(user));
        }

        Instant now = clock.instant();
        String passwordHash = passwordHasher.hash(password);

        UserDomain user = UserDomain.createTopAdmin(
                validatedName,
                validatedEmail,
                passwordHash,
                now);

        return UserResult.from(
                userRepository.save(user));
    }

    @Transactional
    public CompanyAdminProvisionResult provisionCompanyAdmin(
            String name,
            String email
    ) {
        String validatedName = validateName(name);
        String validatedEmail = validateEmail(email);
        String normalizedEmail = Validator.normalizeEmail(validatedEmail);
        Instant now = clock.instant();

        UserDomain user = userRepository
                .findByNormalizedEmail(normalizedEmail)
                .orElseGet(() -> userRepository.save(
                        UserDomain.createPendingUser(
                                validatedName,
                                validatedEmail,
                                passwordHasher.hash(UUID.randomUUID().toString()),
                                now)));

        if (user.isTopAdmin()) {
            throw new InvalidCompanyAdminException(
                    "A top-admin cannot be assigned as company-admin");
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            return new CompanyAdminProvisionResult(
                    UserResult.from(user), null, null);
        }

        if (user.getStatus() != UserStatus.PENDING_ACTIVATION) {
            throw new InvalidCompanyAdminException(
                    "Company-admin account is not active or pending activation");
        }

        GeneratedUserActivationTokenResult generatedToken =
                activationTokenGenerator.generate();
        Instant expiresAt = now.plus(userPolicy.activationTokenTtl());

        activationRepository.save(UserActivationDomain.create(
                user.getId(), generatedToken.hash(), now, expiresAt));

        return new CompanyAdminProvisionResult(
                UserResult.from(user), generatedToken.value(), expiresAt);
    }

    @Transactional
    public UserResult activate(
            String rawActivationToken,
            String password
    ) {
        if (rawActivationToken == null || rawActivationToken.isBlank()) {
            throw new InvalidActivationTokenException();
        }

        validatePassword(password);
        String tokenHash = activationTokenGenerator.hash(rawActivationToken);
        UserActivationDomain activation = activationRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(InvalidActivationTokenException::new);
        Instant now = clock.instant();

        if (!activation.isActive(now)) {
            throw new InvalidActivationTokenException();
        }

        UserDomain user = userRepository.findById(activation.getUserId())
                .orElseThrow(InvalidActivationTokenException::new);

        user.activate(passwordHasher.hash(password), now);
        activation.markUsed(now);
        userRepository.save(user);
        activationRepository.save(activation);

        return UserResult.from(user);
    }

    @Transactional
    public UserResult registerInvitedUser(
            String name,
            String email,
            String password
    ) {
        String validatedName = validateName(name);
        String validatedEmail = validateEmail(email);
        validatePassword(password);

        if (userRepository.existsByNormalizedEmail(
                Validator.normalizeEmail(validatedEmail))) {
            throw new EmailAlreadyExistsException();
        }

        Instant now = clock.instant();
        UserDomain user = UserDomain.createActiveUser(
                validatedName, validatedEmail, passwordHasher.hash(password), now);
        return UserResult.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResult currentUser(UUID userId) {
        UserDomain user = userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return UserResult.from(user);
    }

    private void validatePassword(String password) {
        if (password == null
                || password.length() < PASSWORD_MIN_LENGTH) {
            throw new InvalidRegistrationException(
                    "Password must contain at least "
                            + PASSWORD_MIN_LENGTH
                            + " characters");
        }

        int passwordBytes = password
                .getBytes(StandardCharsets.UTF_8)
                .length;

        if (passwordBytes > PASSWORD_MAX_LENGTH) {
            throw new InvalidRegistrationException(
                    "Password must not exceed "
                            + PASSWORD_MAX_LENGTH
                            + " UTF-8 bytes");
        }
    }

    private String validateName(String name) {
        try {
            return Validator.requireText(name, "name", 100);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRegistrationException(exception.getMessage());
        }
    }

    private String validateEmail(String email) {
        try {
            String validatedEmail = Validator.requireEmail(email);
            if (validatedEmail.length() > 100) {
                throw new IllegalArgumentException(
                        "email must not exceed 100 characters");
            }
            return validatedEmail;
        } catch (IllegalArgumentException exception) {
            throw new InvalidRegistrationException(exception.getMessage());
        }
    }
}
