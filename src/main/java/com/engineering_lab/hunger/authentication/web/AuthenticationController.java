package com.engineering_lab.hunger.authentication.web;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.engineering_lab.hunger.authentication.web.dto.CsrfTokenResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @GetMapping("/csrf")
    public CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return CsrfTokenResponse.from(csrfToken);
    }
}
