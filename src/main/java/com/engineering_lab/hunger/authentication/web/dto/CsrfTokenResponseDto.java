package com.engineering_lab.hunger.authentication.web.dto;

import org.springframework.security.web.csrf.CsrfToken;

public record CsrfTokenResponseDto(
        String token,
        String headerName
) {

    public static CsrfTokenResponseDto from(
            CsrfToken csrfToken
    ) {
        return new CsrfTokenResponseDto(
                csrfToken.getToken(),
                csrfToken.getHeaderName());
    }
}
