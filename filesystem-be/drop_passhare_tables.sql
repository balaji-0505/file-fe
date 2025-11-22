-- Run this SQL in your MySQL client (MySQL Workbench, phpMyAdmin, or command line)
-- This will drop the passhare tables so Hibernate can recreate them with correct schema

USE filesharing;

DROP TABLE IF EXISTS passhare_session_files;
DROP TABLE IF EXISTS passhare_session_participants;
DROP TABLE IF EXISTS passhare_sessions;
