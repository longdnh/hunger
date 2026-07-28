package com.engineering_lab.hunger.user.infra.security;

import java.util.Objects;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.engineering_lab.hunger.user.application.port.PasswordHasherPort;

@Component
public class SpringPasswordHasherAdapter
        implements PasswordHasherPort {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordHasherAdapter() {
        this.passwordEncoder = PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Override
    public String hash(String rawPassword) {
        Objects.requireNonNull(
                rawPassword,
                "rawPassword must not be null");

        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(
            String rawPassword,
            String passwordHash) {
        Objects.requireNonNull(
                rawPassword,
                "rawPassword must not be null");

        Objects.requireNonNull(
                passwordHash,
                "passwordHash must not be null");

        return passwordEncoder.matches(
                rawPassword,
                passwordHash);
    }
}
