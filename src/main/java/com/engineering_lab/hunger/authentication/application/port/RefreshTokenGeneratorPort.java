package com.engineering_lab.hunger.authentication.application.port;

import com.engineering_lab.hunger.authentication.application.result.GeneratedRefreshTokenResult;

public interface RefreshTokenGeneratorPort {

    GeneratedRefreshTokenResult generate();

    String hash(String rawToken);
}
