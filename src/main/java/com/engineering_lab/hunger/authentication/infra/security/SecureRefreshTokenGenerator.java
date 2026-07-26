package com.engineering_lab.hunger.authentication.infra.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.authentication.application.result.GeneratedRefreshToken;
import com.engineering_lab.hunger.authentication.application.security.RefreshTokenGenerator;

@Component
public class SecureRefreshTokenGenerator
                implements RefreshTokenGenerator {

        private static final int TOKEN_SIZE_BYTES = 32;

        private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

        private final SecureRandom secureRandom = new SecureRandom();

        @Override
        public GeneratedRefreshToken generate() {
                byte[] randomBytes = new byte[TOKEN_SIZE_BYTES];

                secureRandom.nextBytes(randomBytes);

                String value = BASE64_URL_ENCODER.encodeToString(
                                randomBytes);

                return new GeneratedRefreshToken(
                                value,
                                hash(value));
        }

        @Override
        public String hash(String rawToken) {
                if (rawToken == null || rawToken.isBlank()) {
                        throw new IllegalArgumentException(
                                        "rawToken must not be blank");
                }

                MessageDigest digest = sha256();

                byte[] hash = digest.digest(
                                rawToken.getBytes(StandardCharsets.UTF_8));

                return BASE64_URL_ENCODER.encodeToString(hash);
        }

        private MessageDigest sha256() {
                try {
                        return MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException exception) {
                        throw new IllegalStateException(
                                        "SHA-256 is not available",
                                        exception);
                }
        }
}
