package com.engineering_lab.hunger.user.infra.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.user.application.port.UserActivationTokenGeneratorPort;
import com.engineering_lab.hunger.user.application.result.GeneratedUserActivationTokenResult;

@Component
public class SecureUserActivationTokenGeneratorAdapter
        implements UserActivationTokenGeneratorPort {

    private static final int TOKEN_SIZE_BYTES = 32;
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public GeneratedUserActivationTokenResult generate() {
        byte[] bytes = new byte[TOKEN_SIZE_BYTES];
        secureRandom.nextBytes(bytes);
        String value = ENCODER.encodeToString(bytes);
        return new GeneratedUserActivationTokenResult(value, hash(value));
    }

    @Override
    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("rawToken must not be blank");
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return ENCODER.encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
