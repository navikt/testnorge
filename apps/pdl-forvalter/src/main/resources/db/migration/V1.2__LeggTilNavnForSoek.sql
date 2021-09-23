-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------
alter table person
    add column fornavn varchar(20),
    add column mellomnavn varchar(20),
    add column etternavn varchar(20);