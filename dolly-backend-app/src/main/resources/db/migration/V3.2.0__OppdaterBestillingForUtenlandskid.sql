-------------------------------
-- U P D A T E   T A B L E S --
-------------------------------
UPDATE T_BESTILLING
SET BEST_KRITERIER = REPLACE(
        REPLACE(BEST_KRITERIER, REGEXP_SUBSTR(BEST_KRITERIER, '"utenlandskIdentifikasjonsnummer":{[-,0-9:"a-zA-Z ]+}'),
                REGEXP_SUBSTR(BEST_KRITERIER, '"utenlandskIdentifikasjonsnummer":{[-,0-9:"a-zA-Z ]+}') || ']'),
        '"utenlandskIdentifikasjonsnummer":{', '"utenlandskIdentifikasjonsnummer":[{')
WHERE BEST_KRITERIER LIKE '%utenlandskIdentifikasjonsnummer%';

COMMIT;