-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BRUKER
    ADD EID_AV_ID NUMBER(9) CONSTRAINT FK_EID_AV_BRUKER REFERENCES T_BRUKER (ID);
ALTER TABLE T_BRUKER
    ADD MIGRERT CHAR(1);

