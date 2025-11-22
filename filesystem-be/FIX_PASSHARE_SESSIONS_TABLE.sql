-- Fix the passhare_sessions table structure
USE filesharing;

-- Check if host_user_id column exists and remove it, or rename it
-- First, let's see the current structure
-- SHOW COLUMNS FROM passhare_sessions;

-- Option 1: If host_user_id exists and creator_id doesn't, rename it
ALTER TABLE passhare_sessions 
CHANGE COLUMN host_user_id creator_id BIGINT NOT NULL;

-- Option 2: If both exist, drop host_user_id
-- ALTER TABLE passhare_sessions DROP COLUMN host_user_id;

-- Option 3: If creator_id doesn't exist and host_user_id does, rename
-- (This is the most likely scenario based on the error)

