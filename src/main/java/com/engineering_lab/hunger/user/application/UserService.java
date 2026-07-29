package com.engineering_lab.hunger.user.application;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.engineering_lab.hunger.common.validator.Validator;
import com.engineering_lab.hunger.user.application.exception.EmailAlreadyExistsException;
import com.engineering_lab.hunger.user.application.exception.InvalidRegistrationException;
import com.engineering_lab.hunger.user.application.exception.UserNotFoundException;
import com.engineering_lab.hunger.user.application.port.PasswordHasherPort;
import com.engineering_lab.hunger.user.application.port.UserRepositoryPort;
import com.engineering_lab.hunger.user.application.result.UserResult;
import com.engineering_lab.hunger.user.domain.model.UserDomain;

@Service
public class UserService {

    private static final int PASSWORD_MIN_LENGTH = 12;
    private static final int PASSWORD_MAX_LENGTH = 72;

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final Clock clock;

    public UserService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    @Transactional
    public UserResult register(
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

        if (userRepository.existsByNormalizedEmail(
                normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        Instant now = clock.instant();
        String passwordHash = passwordHasher.hash(password);

        UserDomain user = UserDomain.create(
                validatedName,
                validatedEmail,
                passwordHash,
                now);

        return UserResult.from(
                userRepository.save(user));
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
}
