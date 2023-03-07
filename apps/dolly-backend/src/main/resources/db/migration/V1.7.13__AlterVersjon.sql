------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table BRUKER
    add column VERSJON smallint not null default 0;

alter table GRUPPE
    add column VERSJON smallint not null default 0;

alter table TEST_IDENT
    add column VERSJON smallint not null default 0;
