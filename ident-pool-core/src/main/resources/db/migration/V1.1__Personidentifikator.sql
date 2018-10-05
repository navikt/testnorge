CREATE TABLE PERSONIDENTIFIKATOR (
  ID                  NUMBER        PRIMARY KEY,
  IDENTTYPE           VARCHAR2(123) NOT NULL,
  PERSONIDENTIFIKATOR VARCHAR2(255) NOT NULL,
  REKVIRERINGSSTATUS  VARCHAR2(255) NOT NULL,
  FINNES_HOS_SKATT    CHAR(1)       NOT NULL,
  FOEDSELSDATO        DATE          NOT NULL,
  KJOENN              VARCHAR2(255) NOT NULL,
  REKVIRERT_AV        VARCHAR2(255)
);

ALTER TABLE PERSONIDENTIFIKATOR
  ADD CONSTRAINT Unique_identifikator UNIQUE (PERSONIDENTIFIKATOR);

CREATE SEQUENCE PERSONIDENTIFIKATOR_SEQ START WITH 1;
