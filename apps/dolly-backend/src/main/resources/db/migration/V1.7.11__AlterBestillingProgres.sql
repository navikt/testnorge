------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling_progress
    add column pdl_person_status varchar(500);

alter table bestilling_progress
    add column VERSJON smallint not null default 0;

alter table bestilling
    alter column miljoer drop not null;

alter table bestilling
    add column VERSJON smallint not null default 0;