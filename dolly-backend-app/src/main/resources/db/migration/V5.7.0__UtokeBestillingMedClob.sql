-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BESTILLING
    ADD (BEST_KRITERIER_NEW CLOB);

------------------------------
-- M I G R A T E   D A T A  --
------------------------------
UPDATE T_BESTILLING
SET BEST_KRITERIER_NEW = BEST_KRITERIER;
COMMIT;

-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BESTILLING
    DROP COLUMN BEST_KRITERIER;
ALTER TABLE T_BESTILLING
    RENAME COLUMN BEST_KRITERIER_NEW TO BEST_KRITERIER;