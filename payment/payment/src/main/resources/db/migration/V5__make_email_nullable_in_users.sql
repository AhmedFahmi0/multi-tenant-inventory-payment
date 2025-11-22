-- Make email column nullable since authentication is now based on username
ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;
