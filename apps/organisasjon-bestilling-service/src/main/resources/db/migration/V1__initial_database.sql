CREATE TABLE ORGANISASJON_ORDER
(
    ID          SERIAL PRIMARY KEY,
    UUID        VARCHAR(256) NOT NULL,
    BATCH_ID    INTEGER      NOT NULL,
    ENVIRONMENT VARCHAR(64)  NOT NULL,
    CREATED_AT  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);