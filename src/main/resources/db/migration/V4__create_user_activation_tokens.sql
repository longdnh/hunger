CREATE TABLE user_activation_tokens (
    user_activation_token_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP(6) WITH TIME ZONE,

    CONSTRAINT pk_user_activation_tokens PRIMARY KEY (user_activation_token_id),
    CONSTRAINT uk_user_activation_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_user_activation_tokens_user FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    CONSTRAINT chk_user_activation_tokens_id_uuidv7
        CHECK (uuid_extract_version(user_activation_token_id) = 7),
    CONSTRAINT chk_user_activation_tokens_hash_not_blank
        CHECK (btrim(token_hash) <> ''),
    CONSTRAINT chk_user_activation_tokens_expiry
        CHECK (expires_at > created_at)
);

CREATE INDEX idx_user_activation_tokens_user_id
    ON user_activation_tokens (user_id);
