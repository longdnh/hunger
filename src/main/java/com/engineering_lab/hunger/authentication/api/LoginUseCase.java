package com.engineering_lab.hunger.authentication.api;

import com.engineering_lab.hunger.authentication.application.command.LoginCommand;
import com.engineering_lab.hunger.authentication.application.result.AuthenticationTokens;

public interface LoginUseCase {

    AuthenticationTokens login(LoginCommand command);
}
