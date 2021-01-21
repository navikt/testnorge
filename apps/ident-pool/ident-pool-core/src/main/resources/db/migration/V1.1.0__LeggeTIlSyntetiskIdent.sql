-----------------------------
-- A L T E R   T A B L E S --
-----------------------------

alter table personidentifikator
    add column syntetisk boolean not null default false;
