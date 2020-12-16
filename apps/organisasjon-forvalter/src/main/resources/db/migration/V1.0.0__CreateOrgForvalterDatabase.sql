-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table organisasjon
(
    id                  integer primary key,
    organisasjonsnummer varchar(9),
    enhetstype          varchar(4),
    naeringskode        varchar(6),
    sektorkode          varchar(4),
    formaal             varchar(70),
    organisasjonsnavn   varchar(150),
    telefon             varchar(22),
    epost               varchar(100),
    nettside            varchar(100),
    maalform            varchar(1),
    parent_org          integer references organisasjon (id)
);

create table adresse
(
    id                 integer primary key,
    organisasjon_id    integer references organisasjon (id),
    adressetype        varchar(15),
    adresse            varchar(300),
    gatekode           varchar(5),
    husnummer          varchar(1),
    boenhet            varchar(5),
    postnr             varchar(9),
    kommunenr          varchar(9),
    landkode           varchar(3)
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence organisasjon_seq;
create sequence adresse_seq;
