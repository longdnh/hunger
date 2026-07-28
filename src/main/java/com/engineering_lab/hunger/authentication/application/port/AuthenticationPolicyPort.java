package com.engineering_lab.hunger.authentication.application.port;

import java.time.Duration;

public interface AuthenticationPolicyPort {

    Duration refreshTokenTtl();
}
