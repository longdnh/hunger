CREATE TABLE users (
    user_id UUID NOT NULL DEFAULT uuidv7(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    normalized_email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email_verified_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_normalized_email UNIQUE (normalized_email),
    CONSTRAINT chk_users_user_id_uuidv7 CHECK (uuid_extract_version(user_id) = 7),
    CONSTRAINT chk_users_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT chk_users_email_not_blank CHECK (btrim(email) <> ''),
    CONSTRAINT chk_users_normalized_email_not_blank CHECK (btrim(normalized_email) <> ''),
    CONSTRAINT chk_users_timestamps CHECK (updated_at >= created_at)
);

CREATE TABLE tenant (
    tenant_id UUID NOT NULL DEFAULT uuidv7(),
    name VARCHAR(100) NOT NULL,
    tenant_code VARCHAR(5) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_tenant PRIMARY KEY (tenant_id),
    CONSTRAINT uk_tenant_tenant_code UNIQUE (tenant_code),
    CONSTRAINT chk_tenant_tenant_id_uuidv7 CHECK (uuid_extract_version(tenant_id) = 7),
    CONSTRAINT chk_tenant_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT chk_tenant_code_not_blank CHECK (btrim(tenant_code) <> ''),
    CONSTRAINT chk_tenant_timestamps CHECK (updated_at >= created_at)
);

CREATE TABLE membership (
    membership_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_membership PRIMARY KEY (membership_id),
    CONSTRAINT uk_membership_user_tenant UNIQUE (user_id, tenant_id),
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_membership_tenant FOREIGN KEY (tenant_id)
        REFERENCES tenant (tenant_id),
    CONSTRAINT chk_membership_membership_id_uuidv7
        CHECK (uuid_extract_version(membership_id) = 7),
    CONSTRAINT chk_membership_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_membership_timestamps CHECK (updated_at >= created_at)
);

CREATE INDEX idx_membership_tenant_id ON membership (tenant_id);

CREATE TABLE user_sessions (
    user_session_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP(6) WITH TIME ZONE,

    CONSTRAINT pk_user_sessions PRIMARY KEY (user_session_id),
    CONSTRAINT uk_user_sessions_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    CONSTRAINT chk_user_sessions_user_session_id_uuidv7
        CHECK (uuid_extract_version(user_session_id) = 7),
    CONSTRAINT chk_user_sessions_token_hash_not_blank CHECK (btrim(token_hash) <> '')
);

CREATE INDEX idx_user_sessions_user_id ON user_sessions (user_id);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions (expires_at);
