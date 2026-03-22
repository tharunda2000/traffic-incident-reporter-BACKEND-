
-- 1. Users Tables

DROP TABLE IF EXISTS users;
CREATE TABLE users (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     username VARCHAR(50) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     email VARCHAR(100) NOT NULL UNIQUE,
     mobile VARCHAR(15) NOT NULL UNIQUE,
     is_active tinyint(1) DEFAULT '0',
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

CREATE TABLE user_roles(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 2. Incidents Table

DROP TABLE IF EXISTS incidents;
CREATE TABLE incidents(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    type ENUM('ACCIDENT','FLOOD', 'ROAD_BLOCK', 'POLICE', 'HAZARD','OTHER') NOT NULL,
    description TEXT,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    status ENUM('PENDING', 'VERIFIED', 'EXPIRED') DEFAULT 'PENDING',
    vote_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,

    CONSTRAINT `fk_incidents_users` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`)
);

-- 3. Votes Table

DROP TABLE IF EXISTS votes;
CREATE TABLE votes(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    incident_id BIGINT,

    CONSTRAINT `uk_user_per_incident` UNIQUE KEY `user_incident` (`user_id`,`incident_id`),
    CONSTRAINT `fk_votes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_votes_incident` FOREIGN KEY (`incident_id`) REFERENCES `incidents` (`id`)

);
