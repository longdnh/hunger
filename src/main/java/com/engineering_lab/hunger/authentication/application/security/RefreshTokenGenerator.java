package com.engineering_lab.hunger.authentication.application.security;

import com.engineering_lab.hunger.authentication.application.result.GeneratedRefreshToken;

public interface RefreshTokenGenerator {

    GeneratedRefreshToken generate();

    String hash(String rawToken);
}
