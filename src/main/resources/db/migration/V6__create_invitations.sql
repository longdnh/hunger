CREATE TABLE invitations (
    invitation_id UUID NOT NULL DEFAULT uuidv7(),
    tenant_id UUID NOT NULL,
    created_by_user_id UUID NOT NULL,
    email VARCHAR(100) NOT NULL,
    normalized_email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    accepted_at TIMESTAMP(6) WITH TIME ZONE,

    CONSTRAINT pk_invitations PRIMARY KEY (invitation_id),
    CONSTRAINT uk_invitations_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_invitations_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (tenant_id),
    CONSTRAINT fk_invitations_creator FOREIGN KEY (created_by_user_id) REFERENCES users (user_id),
    CONSTRAINT chk_invitations_id_uuidv7 CHECK (uuid_extract_version(invitation_id) = 7),
    CONSTRAINT chk_invitations_role CHECK (role IN ('MEMBER', 'GUEST')),
    CONSTRAINT chk_invitations_expiry CHECK (expires_at > created_at)
);

CREATE INDEX idx_invitations_tenant_email
    ON invitations (tenant_id, normalized_email);
