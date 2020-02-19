------------------------------
-- M I G R A T E   D A T A  --
------------------------------

UPDATE t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"Summert skattegrunnlag","grunnlag"', '"SUMMERT_SKATTEGRUNNLAG"');

Update t_bestilling
SET best_kriterier = REPLACE(best_kriterier, '"Beregnet skatt","grunnlag"', '"BEREGNET_SKATT"');

COMMIT;