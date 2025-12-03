-- Create database if not exists
SELECT 'CREATE DATABASE goormthon'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'goormthon')\gexec

-- Connect to goormthon database
\c goormthon

-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable cryptographic functions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Log extensions
SELECT extname, extversion FROM pg_extension;
