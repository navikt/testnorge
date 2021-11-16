------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bruker
    add column brukertype varchar(6) not null default 'AZURE';

update bruker
set brukertype = 'BANKID'
where bruker_id NOT LIKE '%-%';

update bruker
set brukertype = 'BASIC'
where nav_ident is not null;

commit;