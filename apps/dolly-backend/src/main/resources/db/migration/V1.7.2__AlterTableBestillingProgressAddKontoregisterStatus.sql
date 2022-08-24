------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling_progress
    add kontoregister_status varchar(4000);

commit;
