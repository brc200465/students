CREATE DATABASE IF NOT EXISTS studentms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE studentms;

CREATE TABLE IF NOT EXISTS student (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT
);

CREATE TABLE IF NOT EXISTS sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    username VARCHAR(50),
    operation_type VARCHAR(50),
    operation_desc VARCHAR(255),
    request_ip VARCHAR(50),
    success TINYINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);