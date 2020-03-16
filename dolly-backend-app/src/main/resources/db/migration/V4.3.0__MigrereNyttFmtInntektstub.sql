-----------------------------
-- M I G R A T E   D A T A --
-----------------------------

UPDATE t_bestilling
SET best_kriterier = REPLACE(best_kriterier, 'startAarMaaned', 'sisteAarMaaned')
WHERE best_kriterier LIKE '%startAarMaaned%';

COMMIT;