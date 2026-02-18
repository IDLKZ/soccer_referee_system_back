CREATE TABLE IF NOT EXISTS files
(
    id             BIGSERIAL   PRIMARY KEY,
    original_name  TEXT        NOT NULL,
    unique_name    TEXT        NOT NULL,
    full_path      TEXT        NOT NULL,
    directory      TEXT        NULL,
    extension      TEXT        NULL,
    "mimeType"     TEXT        NULL,
    stored_local   BOOLEAN     NOT NULL DEFAULT TRUE,
    file_size_byte BIGINT      NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
