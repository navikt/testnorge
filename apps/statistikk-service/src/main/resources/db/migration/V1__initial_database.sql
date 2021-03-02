create table statistikk
(
    type        VARCHAR(100) PRIMARY KEY,
    description VARCHAR(256),
    value       DECIMAL      NOT NULL,
    value_type  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);