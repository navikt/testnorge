------------------------------
-- M I G R A T E   D A T A  --
------------------------------

UPDATE t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"kjoenn":"UBESTEMT"', '"kjoenn":"UKJENT"')
WHERE best_kriterier LIKE '%"kjoenn":"UBESTEMT"%';

COMMIT;