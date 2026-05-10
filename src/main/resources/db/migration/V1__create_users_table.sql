CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,

                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,

                       password TEXT NOT NULL,

                       age INTEGER NOT NULL,

                       role VARCHAR(50) NOT NULL,

                       gender VARCHAR(20) NOT NULL,

                       avatar TEXT
);