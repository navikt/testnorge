CREATE TABLE BATCH (
  ID                    NUMBER          PRIMARY KEY,
  STATUS                VARCHAR2        NOT NULL,
  SISTOPPDATERT         DATE                    ,
  FEILMELDING           VARCHAR2                ,
);

CREATE SEQUENCE BATCH_SEQ START WITH 1;