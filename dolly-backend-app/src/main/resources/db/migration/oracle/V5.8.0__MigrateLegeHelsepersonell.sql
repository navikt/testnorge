------------------------------
-- M I G R A T E   D A T A  --
------------------------------

UPDATE t_bestilling
SET best_kriterier = REPLACE(best_kriterier, 'lege', 'helsepersonell')
WHERE best_kriterier LIKE '%"lege"%';

COMMIT;