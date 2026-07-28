package com.engineering_lab.hunger.authentication.application.port;

import java.util.UUID;

import com.engineering_lab.hunger.authentication.application.result.IssuedAccessTokenResult;

public interface AccessTokenIssuerPort {

    IssuedAccessTokenResult issue(
            UUID userId,
            UUID sessionId);
}
