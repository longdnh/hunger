ALTER TABLE membership
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'MEMBER';

ALTER TABLE membership
    ALTER COLUMN role DROP DEFAULT;

ALTER TABLE membership
    ADD CONSTRAINT chk_membership_role
        CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER', 'GUEST'));
