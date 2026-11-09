-- Lab 7 database for the Swing user registration application.
-- Run this once, for example in MySQL Workbench, before starting the application.

CREATE DATABASE IF NOT EXISTS lab_db CHARACTER SET utf8mb4;
USE lab_db;

CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    gender        VARCHAR(20)  NOT NULL,
    date_of_birth DATE         NOT NULL,
    phone         VARCHAR(20),
    email         VARCHAR(100) NOT NULL UNIQUE,
    continent     VARCHAR(30)  NOT NULL,
    experience    TEXT,
    hobbies       TEXT         NOT NULL,
    photo_path    VARCHAR(255)
);

-- Age is not stored. The application works it out from date_of_birth.

-- Handy checks:
-- DESCRIBE users;
-- SELECT * FROM users;
