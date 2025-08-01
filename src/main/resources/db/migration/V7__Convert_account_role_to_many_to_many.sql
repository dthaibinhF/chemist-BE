-- Convert Account-Role relationship from One-to-One to Many-to-Many
-- Create junction table for account_roles relationship

-- Step 1: Create the junction table account_roles
CREATE TABLE account_roles (
    account_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (account_id, role_id),
    CONSTRAINT fk_account_roles_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_account_roles_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- Step 2: Migrate existing data from account.role_id to account_roles junction table
INSERT INTO account_roles (account_id, role_id)
SELECT id, role_id 
FROM account 
WHERE role_id IS NOT NULL 
  AND end_at IS NULL;  -- Only migrate active accounts

-- Step 3: Create indexes for better performance
CREATE INDEX idx_account_roles_account_id ON account_roles(account_id);
CREATE INDEX idx_account_roles_role_id ON account_roles(role_id);

-- Step 4: Remove the old role_id column from account table
-- Note: We'll do this in multiple steps for safety
ALTER TABLE account DROP CONSTRAINT IF EXISTS fk_account_role_id;  -- Remove foreign key if exists
ALTER TABLE account DROP COLUMN role_id;

-- Add comment for documentation
COMMENT ON TABLE account_roles IS 'Junction table for many-to-many relationship between accounts and roles';
COMMENT ON COLUMN account_roles.account_id IS 'Foreign key reference to account table';
COMMENT ON COLUMN account_roles.role_id IS 'Foreign key reference to role table';