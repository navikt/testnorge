------------------------------
-- M I G R A T E   D A T A  --
------------------------------

UPDATE t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"Summert skattegrunnlag","grunnlag"', '"SUMMERT_SKATTEGRUNNLAG"')
WHERE best_kriterier LIKE '%"Summert skattegrunnlag","grunnlag"%';

Update t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"Beregnet skatt","grunnlag"', '"BEREGNET_SKATT"')
WHERE best_kriterier LIKE '%"Beregnet skatt","grunnlag"%';

COMMIT;