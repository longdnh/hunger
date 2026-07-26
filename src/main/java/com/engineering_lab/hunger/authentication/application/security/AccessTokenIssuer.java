package com.engineering_lab.hunger.authentication.application.security;

import java.util.UUID;

import com.engineering_lab.hunger.authentication.application.result.IssuedAccessToken;

public interface AccessTokenIssuer {

    IssuedAccessToken issue(
            UUID userId,
            UUID sessionId);
}
