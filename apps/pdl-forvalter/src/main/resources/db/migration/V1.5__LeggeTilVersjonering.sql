-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------

alter table person
    add column versjon smallint default 0;

alter table relasjon
    add column versjon smallint default 0;

alter table alias
    add column versjon smallint default 0;