DROP TABLE APPLICATION_ENTITY;

CREATE TABLE DOCUMENT_ENTITY
(
    SHA        VARCHAR(256) PRIMARY KEY,
    CONTENT    TEXT         NOT NULL,
    OWNER      VARCHAR(256) NOT NULL,
    REPO       VARCHAR(256) NOT NULL,
    TYPE       VARCHAR(256) NOT NULL,
    PATH       VARCHAR(256) NOT NULL,
    CREATED_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);