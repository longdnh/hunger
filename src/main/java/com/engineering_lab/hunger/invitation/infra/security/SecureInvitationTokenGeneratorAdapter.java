package com.engineering_lab.hunger.invitation.infra.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.invitation.application.port.InvitationTokenGeneratorPort;
import com.engineering_lab.hunger.invitation.application.result.GeneratedInvitationTokenResult;

@Component
public class SecureInvitationTokenGeneratorAdapter implements InvitationTokenGeneratorPort {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private final SecureRandom random = new SecureRandom();
    @Override
    public GeneratedInvitationTokenResult generate() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String value = ENCODER.encodeToString(bytes);
        return new GeneratedInvitationTokenResult(value, hash(value));
    }
    @Override
    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) throw new IllegalArgumentException("rawToken must not be blank");
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return ENCODER.encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
