CREATE TABLE AJOURHOLD (
  ID                    NUMBER          PRIMARY KEY,
  STATUS                VARCHAR2        NOT NULL,
  SISTOPPDATERT         DATE                    ,
  FEILMELDING           VARCHAR2                ,
);

CREATE SEQUENCE AJOURHOLD_SEQ START WITH 1;