------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling_progress
    alter column inntektsstub_status type varchar(2048);

alter table bestilling_progress
   alter column master drop default;

alter table test_ident
    alter column master drop default;