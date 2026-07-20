package com.engineering_lab.hunger.tenant.api;

import com.engineering_lab.hunger.tenant.application.command.CreateTenantCommand;
import com.engineering_lab.hunger.tenant.application.result.CreatedTenant;

public interface CreateTenantUseCase {
    CreatedTenant execute(CreateTenantCommand command);
}
