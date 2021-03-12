-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------

alter table bestilling
    add column pdl_import text;

alter table bestilling_progress
    add column pdl_import_status varchar(500),
    add column master varchar(4) not null default 'TPSF';

alter table test_ident
    add column master varchar(4) not null default 'TPSF';