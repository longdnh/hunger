package com.engineering_lab.hunger.authentication.api;

import com.engineering_lab.hunger.authentication.application.command.LoginCommand;
import com.engineering_lab.hunger.authentication.application.result.LoginResult;

public interface LoginUseCase {

    LoginResult login(LoginCommand command);
}
