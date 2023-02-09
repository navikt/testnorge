------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling_progress
    add column VERSJON bigint not null default 0;