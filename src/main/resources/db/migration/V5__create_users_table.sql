-- V5: Create users table

CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL    PRIMARY KEY,
    role_id       BIGINT       NULL REFERENCES roles(id) ON DELETE SET NULL,
    image_id      BIGINT       NULL REFERENCES files(id) ON DELETE SET NULL,
    username      VARCHAR(280) NOT NULL,
    phone         VARCHAR(255) NULL,
    email         VARCHAR(280) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    patronymic    VARCHAR(255) NULL,
    password_hash TEXT         NULL,
    birth_date    DATE         NULL,
    gender        INTEGER      NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    is_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP    NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);
