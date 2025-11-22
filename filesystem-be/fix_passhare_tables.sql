-- Fix passhare tables schema issue
-- Run this SQL script in your MySQL database to drop and let Hibernate recreate the tables

USE filesharing;

-- Drop tables in correct order (due to foreign key constraints)
DROP TABLE IF EXISTS passhare_session_files;
DROP TABLE IF EXISTS passhare_session_participants;
DROP TABLE IF EXISTS passhare_sessions;

-- After running this, restart your Spring Boot application
-- Hibernate will recreate the tables with the correct schema using ddl-auto=update

