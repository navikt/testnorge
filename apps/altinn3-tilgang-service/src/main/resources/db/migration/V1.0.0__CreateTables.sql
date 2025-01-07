-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table organisasjon_tilgang
(
    id                 serial primary key,
    organisajon_nummer varchar(50) unique,
    miljoe             varchar(100)
);