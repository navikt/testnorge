CREATE TABLE APPLICATION_ENTITY
(
    SHA        VARCHAR(256) PRIMARY KEY,
    CONTENT    TEXT         NOT NULL,
    REPO       VARCHAR(256) NOT NULL,
    CREATED_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);