------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling_progress
    add column kontoregister_status varchar(4000);

commit;
