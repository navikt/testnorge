-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------

alter table person
    add column versjon integer default 0;

alter table relasjon
    add column versjon integer default 0;

alter table alias
    add column versjon integer default 0;