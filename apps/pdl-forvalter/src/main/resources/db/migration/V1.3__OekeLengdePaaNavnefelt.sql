-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------
alter table person
    alter column fornavn type varchar(35),
    alter column mellomnavn type varchar(35),
    alter column etternavn type varchar(35);