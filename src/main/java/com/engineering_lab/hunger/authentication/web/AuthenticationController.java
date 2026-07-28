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

import com.engineering_lab.hunger.authentication.application.AuthenticationService;
import com.engineering_lab.hunger.authentication.application.result.AuthenticationResult;
import com.engineering_lab.hunger.authentication.web.cookie.RefreshTokenCookieFactory;
import com.engineering_lab.hunger.authentication.web.dto.CsrfTokenResponseDto;
import com.engineering_lab.hunger.authentication.web.dto.LoginRequestDto;
import com.engineering_lab.hunger.authentication.web.dto.LoginResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;

    public AuthenticationController(
            AuthenticationService authenticationService,
            RefreshTokenCookieFactory refreshTokenCookieFactory) {
        this.authenticationService = authenticationService;
        this.refreshTokenCookieFactory = refreshTokenCookieFactory;
    }

    @GetMapping("/csrf")
    public CsrfTokenResponseDto csrf(CsrfToken csrfToken) {
        return CsrfTokenResponseDto.from(csrfToken);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        AuthenticationResult result = authenticationService.login(
                request.email(),
                request.password());

        ResponseCookie refreshTokenCookie = refreshTokenCookieFactory.create(
                result.refreshToken(),
                result.refreshTokenExpiresAt());

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString())
                .body(LoginResponseDto.from(result));
    }

}
