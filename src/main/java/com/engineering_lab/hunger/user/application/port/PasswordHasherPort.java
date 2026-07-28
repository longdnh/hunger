package com.engineering_lab.hunger.user.application.port;

public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(
            String rawPassword,
            String passwordHash);
}
