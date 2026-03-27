-- Run once on the Auth-Service MySQL database (table `users`) if registration still fails
-- with "email_verified doesn't have a default value".
-- Login/register in the app use username + password only; this column is technical storage.

ALTER TABLE users
    MODIFY COLUMN email_verified TINYINT(1) NOT NULL DEFAULT 0;

-- Optional: backfill any existing NULLs (if the column was nullable before)
-- UPDATE users SET email_verified = 0 WHERE email_verified IS NULL;
