CREATE TABLE PERSONIDENTIFIKATOR (
  ID                    NUMBER          PRIMARY KEY,
  IDENTTYPE             VARCHAR2(123)   NOT NULL,
  PERSONIDENTIFIKATOR   VARCHAR2(255)   NOT NULL,
  REKVIRERINGSSTATUS    VARCHAR2(255)   NOT NULL,
  FINNES_HOS_SKATT      VARCHAR2(255)   NOT NULL
);