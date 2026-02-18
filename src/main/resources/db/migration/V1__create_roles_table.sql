-- V1: Create roles table

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    title_ru VARCHAR(255) NOT NULL,
    title_kk VARCHAR(255) NULL,
    title_en VARCHAR(255) NULL,
    description_ru TEXT NULL,
    description_kk TEXT NULL,
    description_en TEXT NULL,
    value VARCHAR(280) NOT NULL,
    "isSystem" BOOLEAN NOT NULL DEFAULT FALSE,
    "isAdministrative" BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_roles_value ON roles(value);
CREATE INDEX IF NOT EXISTS idx_roles_system ON roles("isSystem");
CREATE INDEX IF NOT EXISTS idx_roles_administrative ON roles("isAdministrative");
