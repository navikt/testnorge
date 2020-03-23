-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BESTILLING_PROGRESS
    ADD (
        INNTEKTSMELDING_STATUS VARCHAR2(2000)
        );