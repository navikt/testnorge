-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE t_bestilling_progress
RENAME COLUMN pensjon_status TO pensjonforvalter_status;

ALTER TABLE t_bestilling_progress
    MODIFY pensjonforvalter_status VARCHAR2(4000);

------------------------------
-- M I G R A T E   D A T A  --
------------------------------
UPDATE t_bestilling_progress
SET pensjonforvalter_status = 'PensjonForvalter#q2:' || replace(pensjonforvalter_status, 'Feil:', 'Feil=')
WHERE pensjonforvalter_status is not null;

COMMIT;