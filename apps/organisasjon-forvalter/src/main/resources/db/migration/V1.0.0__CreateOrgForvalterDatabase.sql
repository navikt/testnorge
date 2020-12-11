-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table organisasjon
(
    id                  integer primary key,
    organisasjonsnummer varchar(9),
    organisasjonsform   varchar(5),
    naeringskode        varchar(5),
    formaal             varchar(100),
    organisasjonsnavn   varchar(150),
    telefon             varchar(22),
    epost               varchar(100),
    nettside            varchar(100),

    parent_org         integer references organisasjon (id)
);

create table adresse
(
    id                 integer primary key,
    organisasjon_id    integer references organisasjon (id),
    adressetype        varchar(5),
    adresse            varchar(300),
    gatekode           integer,
    husnummer          varchar(1)
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
