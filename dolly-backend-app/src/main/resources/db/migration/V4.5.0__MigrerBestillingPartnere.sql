-----------------------------
-- M I G R A T E   D A T A --
-----------------------------

UPDATE t_bestilling
SET tpsf_kriterier = REPLACE(tpsf_kriterier, '"partner":[', '"partnere":[')
WHERE tpsf_kriterier LIKE '%"partner":[%';

COMMIT;