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
import com.engineering_lab.hunger.authentication.application.exception.InvalidRefreshTokenException;
import com.engineering_lab.hunger.authentication.application.result.AuthenticationResult;
import com.engineering_lab.hunger.authentication.web.cookie.RefreshTokenCookieManager;
import com.engineering_lab.hunger.authentication.web.dto.AuthenticationResponseDto;
import com.engineering_lab.hunger.authentication.web.dto.CsrfTokenResponseDto;
import com.engineering_lab.hunger.authentication.web.dto.LoginRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenCookieManager refreshTokenCookieManager;

    public AuthenticationController(
            AuthenticationService authenticationService,
            RefreshTokenCookieManager refreshTokenCookieManager
    ) {
        this.authenticationService = authenticationService;
        this.refreshTokenCookieManager = refreshTokenCookieManager;
    }

    @GetMapping("/csrf")
    public CsrfTokenResponseDto csrf(CsrfToken csrfToken) {
        return CsrfTokenResponseDto.from(csrfToken);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        AuthenticationResult result = authenticationService.login(
                request.email(),
                request.password());

        return authenticationResponse(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDto> refresh(
            HttpServletRequest request
    ) {
        String rawRefreshToken = refreshTokenCookieManager
                .read(request)
                .orElseThrow(
                        InvalidRefreshTokenException::new);

        AuthenticationResult result =
                authenticationService.refresh(
                        rawRefreshToken);

        return authenticationResponse(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {
        authenticationService.logout(
                refreshTokenCookieManager
                        .read(request)
                        .orElse(null));

        ResponseCookie deletedRefreshTokenCookie =
                refreshTokenCookieManager.delete();

        return ResponseEntity
                .noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        deletedRefreshTokenCookie.toString())
                .build();
    }

    private ResponseEntity<AuthenticationResponseDto> authenticationResponse(
            AuthenticationResult result
    ) {
        ResponseCookie refreshTokenCookie =
                refreshTokenCookieManager.create(
                        result.refreshToken(),
                        result.refreshTokenExpiresAt());

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString())
                .body(AuthenticationResponseDto.from(result));
    }
}
