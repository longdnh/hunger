package com.engineering_lab.hunger.user.domain.security;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(
            String rawPassword,
            String passwordHash);
}
