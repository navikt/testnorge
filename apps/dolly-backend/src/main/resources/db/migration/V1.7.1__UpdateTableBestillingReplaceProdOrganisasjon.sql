--------------------------------
-- U P D A T E   T A B L E S  --
--------------------------------

update bestilling
set best_kriterier = REPLACE(best_kriterier, '"virksomhetsnummer":"824771332"', '"virksomhetsnummer":"947064649"');
update bestilling
set best_kriterier = REPLACE(best_kriterier, '"virksomhet":"824771332"', '"virksomhet":"947064649"');
update bestilling
set best_kriterier = REPLACE(best_kriterier, '"orgnummer":"824771332"', '"orgnummer":"947064649"');
commit;