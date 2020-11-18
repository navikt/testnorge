-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
CREATE TABLE T_TRANSAKSJON_MAPPING
(
    ID             NUMBER(9) PRIMARY KEY,
    IDENT          VARCHAR2(11)  NOT NULL,
    SYSTEM         VARCHAR2(20)  NOT NULL,
    MILJOE         VARCHAR2(10)  NOT NULL,
    TRANSAKSJON_ID VARCHAR2(50)  NOT NULL,
    DATO_ENDRET    DATE          NOT NULL
);

-----------------------
-- S E Q U E N C E S --
-----------------------
CREATE SEQUENCE T_TRANSAKSJON_MAPPING_SEQ START WITH 1;