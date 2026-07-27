package com.engineering_lab.hunger.authentication.application.command;

public final class LoginCommand {

    private final String email;
    private final String password;

    public LoginCommand(
            String email,
            String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "email must not be blank");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "password must not be blank");
        }

        this.email = email;
        this.password = password;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }
}
