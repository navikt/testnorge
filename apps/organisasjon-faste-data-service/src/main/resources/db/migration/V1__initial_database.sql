CREATE TABLE ORGANISASJON
(
    ORGNUMMER  VARCHAR(64) PRIMARY KEY,
    DOKUMENT   TEXT        NOT NULL,
    OVERENHET  VARCHAR(64),
    GRUPPE     VARCHAR(64) NOT NULL,
    CREATED_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ORGANISASJON_OVERENHET_ORGNUMMER_FKEY FOREIGN KEY (OVERENHET)
        REFERENCES ORGANISASJON (ORGNUMMER)
);