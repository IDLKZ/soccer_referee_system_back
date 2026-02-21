-- V6: Create auth_sessions table

CREATE TABLE IF NOT EXISTS auth_sessions
(
    id               BIGSERIAL    PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_name      VARCHAR(255) NULL,
    device_os        VARCHAR(255) NULL,
    device_type      VARCHAR(255) NULL,
    browser          VARCHAR(255) NULL,
    ip_address       VARCHAR(255) NULL,
    user_agent       TEXT         NULL,
    country          VARCHAR(255) NULL,
    city             VARCHAR(255) NULL,
    expires_at       BIGINT       NOT NULL,
    revoked          BOOLEAN      NOT NULL DEFAULT FALSE,
    revoked_at       BIGINT       NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auth_sessions_user_id ON auth_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_auth_sessions_revoked ON auth_sessions(revoked);
