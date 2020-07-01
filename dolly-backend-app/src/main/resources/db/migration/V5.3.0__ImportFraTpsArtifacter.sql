-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BESTILLING
    ADD (
        KILDE_MILJOE VARCHAR2(10)
        );

ALTER TABLE T_BESTILLING_PROGRESS
    ADD (
        TPS_IMPORT_STATUS VARCHAR2(500)
        );