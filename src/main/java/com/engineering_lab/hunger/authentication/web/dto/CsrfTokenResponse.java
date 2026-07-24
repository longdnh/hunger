package com.engineering_lab.hunger.authentication.web.dto;

import org.springframework.security.web.csrf.CsrfToken;

public record CsrfTokenResponse(
        String token,
        String headerName) {

    public static CsrfTokenResponse from(CsrfToken csrfToken) {
        return new CsrfTokenResponse(
                csrfToken.getToken(),
                csrfToken.getHeaderName());
    }
}
