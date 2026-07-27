package com.engineering_lab.hunger.authentication.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.engineering_lab.hunger.authentication.api.LoginUseCase;
import com.engineering_lab.hunger.authentication.application.command.LoginCommand;
import com.engineering_lab.hunger.authentication.application.result.LoginResult;
import com.engineering_lab.hunger.authentication.web.cookie.RefreshTokenCookieFactory;
import com.engineering_lab.hunger.authentication.web.dto.CsrfTokenResponse;
import com.engineering_lab.hunger.authentication.web.dto.LoginRequest;
import com.engineering_lab.hunger.authentication.web.dto.LoginResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;

    public AuthenticationController(
            LoginUseCase loginUseCase,
            RefreshTokenCookieFactory refreshTokenCookieFactory) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenCookieFactory = refreshTokenCookieFactory;
    }

    @GetMapping("/csrf")
    public CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return CsrfTokenResponse.from(csrfToken);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(
                request.email(),
                request.password());

        LoginResult result = loginUseCase.login(command);

        ResponseCookie refreshTokenCookie = refreshTokenCookieFactory.create(
                result.refreshToken(),
                result.refreshTokenExpiresAt());

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString())
                .body(LoginResponse.from(result));
    }

}
