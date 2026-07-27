package com.engineering_lab.hunger.authentication.application.security;

import java.time.Duration;

public interface AuthenticationPolicy {

    Duration refreshTokenTtl();
}
