------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bestilling_progress
    alter column id ADD GENERATED ALWAYS AS IDENTITY;

alter table bestilling_progress
    add column pdl_person_status varchar(500);

alter table bestilling_progress
    add column VERSJON smallint not null default 0;

alter table bestilling
    alter column id ADD GENERATED ALWAYS AS IDENTITY;

alter table bestilling
    alter column miljoer drop not null;

alter table bestilling
    add column VERSJON smallint not null default 0;


-------------------------------------
-- U P D A T E   S E Q U E N C E S --
-------------------------------------

select setval('bestilling_id_seq', (select max(id) + 1 from bestilling));
select setval('bestilling_progress_id_seq', (select max(id) + 1 from bestilling_progress));

commit;


---------------------------------
-- D R O P   S E Q U E N C E S --
---------------------------------

drop sequence bestilling_seq;
drop sequence bestilling_progress_seq;