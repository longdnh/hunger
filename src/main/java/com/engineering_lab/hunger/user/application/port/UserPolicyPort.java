package com.engineering_lab.hunger.user.application.port;

import java.time.Duration;

public interface UserPolicyPort {

    Duration activationTokenTtl();
}
