package com.engineering_lab.hunger.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDto(

        @NotBlank(message = "name must not be blank")
        @Size(
                max = 100,
                message = "name must not exceed 100 characters")
        String name,

        @NotBlank(message = "email must not be blank")
        @Email(message = "email is not valid")
        @Size(
                max = 100,
                message = "email must not exceed 100 characters")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(
                min = 12,
                max = 72,
                message = "password must contain 12 to 72 characters")
        String password
) {

    @Override
    public String toString() {
        return """
                RegisterUserRequestDto[
                    name=%s,
                    email=[REDACTED],
                    password=[REDACTED]
                ]
                """.formatted(name);
    }
}
