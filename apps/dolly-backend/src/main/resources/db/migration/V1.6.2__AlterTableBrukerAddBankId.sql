------------------------------
-- A L T E R   T A B L E S  --
------------------------------

alter table bruker
    add column bankid_brukernavn varchar(100),
    add column bankid_token      varchar(50) unique;