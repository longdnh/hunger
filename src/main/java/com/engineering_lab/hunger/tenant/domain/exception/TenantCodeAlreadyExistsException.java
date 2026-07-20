package com.engineering_lab.hunger.tenant.domain.exception;

public class TenantCodeAlreadyExistsException extends RuntimeException {
    public TenantCodeAlreadyExistsException(String tenantCode) {
        super("Tenant code '" + tenantCode + "' is already in use");
    }
}
