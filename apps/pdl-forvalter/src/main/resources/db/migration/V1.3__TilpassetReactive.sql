-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------
alter table person
alter column id set default nextval('person_sequence');

alter table relasjon
alter column id set default nextval('relasjon_sequence');

alter table alias
alter column id set default nextval('alias_sequence');