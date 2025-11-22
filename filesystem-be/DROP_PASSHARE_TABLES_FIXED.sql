-- Drop passhare tables in correct order to handle foreign key constraints
USE filesharing;

-- First, drop the foreign key constraint or the referencing table
DROP TABLE IF EXISTS passhare_participants;
DROP TABLE IF EXISTS passhare_session_files;
DROP TABLE IF EXISTS passhare_session_participants;
DROP TABLE IF EXISTS passhare_sessions;

-- Alternative: If you want to keep data, drop foreign keys first:
-- ALTER TABLE passhare_participants DROP FOREIGN KEY FK66l5u201drno7n0g7t1mhgnhn;
-- Then drop the tables

