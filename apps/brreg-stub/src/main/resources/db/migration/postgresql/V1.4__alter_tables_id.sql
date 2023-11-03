-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
drop sequence rolleoversikt_seq;
drop sequence hentrolle_seq;

alter table rolleoversikt
    alter column id type serial using id::serial;

alter table hent_rolle
    alter column id type serial using id::serial;