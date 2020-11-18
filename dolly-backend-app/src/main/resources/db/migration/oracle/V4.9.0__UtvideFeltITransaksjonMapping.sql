-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_TRANSAKSJON_MAPPING
    MODIFY (
        TRANSAKSJON_ID VARCHAR2(300)
        );

-----------------------------
-- M I G R A T E   D A T A --
-----------------------------
UPDATE T_TRANSAKSJON_MAPPING
SET TRANSAKSJON_ID = '{"journalpostId":"' || TRANSAKSJON_ID || '"}'
WHERE TRANSAKSJON_ID NOT LIKE '%{%';

COMMIT;