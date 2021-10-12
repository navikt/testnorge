-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------
alter table person
alter column id type bigserial primary key;

alter table relasjon
alter column id type bigserial primary key;

alter table alias
alter column id type bigserial primary key;