------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling
drop column tpsf_kriterier;

alter table bestilling
drop column tps_import;

alter table bestilling_progress
drop column tpsf_success_environments;

alter table bestilling_progress
drop column tps_import_status;

alter table bestilling_progress
drop column pdlforvalter_status;

alter table bestilling_progress
rename column pdl_data_status to pdl_ordre_status;

alter table bestilling_progress
add column pdl_forvalter_status varchar(500);