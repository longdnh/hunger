package com.engineering_lab.hunger.authentication.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "email must not be blank") @Email(message = "email is not valid") @Size(max = 254, message = "email must not exceed 254 characters") String email,

        @NotBlank(message = "password must not be blank") @Size(max = 1024, message = "password is too long") String password) {

    @Override
    public String toString() {
        return "LoginRequest[email=[REDACTED], password=[REDACTED]]";
    }
}
