-- ============================================================================
-- Auth-Service: `users` table — username + password only (no email in this app).
--
-- Run ONCE on the MySQL database used by Auth-Service (e.g. mapping_db).
-- Pick ONE option below.
-- ============================================================================

-- Option A (recommended): remove the column entirely
ALTER TABLE users DROP COLUMN email_verified;

-- Option B: if you cannot drop the column, make it optional so INSERTs without it succeed
-- (only use if DROP is not allowed by your ops policy)
-- ALTER TABLE users MODIFY COLUMN email_verified TINYINT(1) NULL DEFAULT NULL;
