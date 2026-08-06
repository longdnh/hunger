UPDATE membership
SET role = 'ADMIN'
WHERE role = 'OWNER';

ALTER TABLE membership
    DROP CONSTRAINT chk_membership_role;

ALTER TABLE membership
    ADD CONSTRAINT chk_membership_role
        CHECK (role IN ('ADMIN', 'MEMBER', 'GUEST'));
