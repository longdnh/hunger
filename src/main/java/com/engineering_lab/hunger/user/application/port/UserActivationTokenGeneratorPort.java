package com.engineering_lab.hunger.user.application.port;

import com.engineering_lab.hunger.user.application.result.GeneratedUserActivationTokenResult;

public interface UserActivationTokenGeneratorPort {

    GeneratedUserActivationTokenResult generate();

    String hash(String rawToken);
}
