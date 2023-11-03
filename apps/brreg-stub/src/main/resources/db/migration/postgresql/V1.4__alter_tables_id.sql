-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
alter table rolleoversikt
    alter id add generated always as identity;

alter table hent_rolle
    alter id add generated always as identity;

select setval(pg_get_serial_sequence('rolleoversikt', 'id'), (select max(id) from rolleoversikt));
select setval(pg_get_serial_sequence('hent_rolle', 'id'), (select max(id) from hent_rolle));