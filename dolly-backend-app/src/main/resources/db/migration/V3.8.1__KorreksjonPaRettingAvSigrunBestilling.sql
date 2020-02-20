------------------------------
-- M I G R A T E   D A T A  --
------------------------------

UPDATE t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"SUMMERT_SKATTEGRUNNLAG"', '"SUMMERT_SKATTEGRUNNLAG","grunnlag"')
WHERE best_kriterier LIKE '%"SUMMERT_SKATTEGRUNNLAG":%';

Update t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"BEREGNET_SKATT"', '"BEREGNET_SKATT","grunnlag"')
WHERE best_kriterier LIKE '%"BEREGNET_SKATT":%';

COMMIT;