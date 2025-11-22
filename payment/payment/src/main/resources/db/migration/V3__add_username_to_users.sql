-- Add username column to users table as NULLABLE first, so existing rows don't break
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS username VARCHAR(255);

-- Backfill username for existing users (copy from email where username is NULL)
UPDATE users
SET username = email
WHERE username IS NULL;

-- Now enforce NOT NULL and UNIQUE on username
ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

-- Create an index on the username column for faster lookups (idempotent)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relname = 'idx_users_username'
          AND c.relkind = 'i'
    ) THEN
        CREATE INDEX idx_users_username ON users(username);
    END IF;
END $$;

-- Drop the unique constraint on email since username is now the unique identifier
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS uk_6dotkott2kjsp8vw4d0m25fb7;