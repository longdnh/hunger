ALTER TABLE users
    ADD COLUMN platform_role VARCHAR(20) NOT NULL DEFAULT 'USER',
    ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users
    ALTER COLUMN platform_role DROP DEFAULT,
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE users
    ADD CONSTRAINT chk_users_platform_role
        CHECK (platform_role IN ('TOP_ADMIN', 'USER')),
    ADD CONSTRAINT chk_users_status
        CHECK (
            status IN (
                'PENDING_ACTIVATION',
                'ACTIVE',
                'INACTIVE',
                'SUSPENDED'
            )
        );
