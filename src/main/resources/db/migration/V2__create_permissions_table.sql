-- V2: Create permissions table

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    title_ru VARCHAR(255) NOT NULL,
    title_kk VARCHAR(255) NULL,
    title_en VARCHAR(255) NULL,
    value VARCHAR(280) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_permissions_value ON permissions(value);
