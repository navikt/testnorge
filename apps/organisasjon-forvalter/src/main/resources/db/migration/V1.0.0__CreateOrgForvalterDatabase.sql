-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table organisasjon
(
    id                 integer primary key,
    organisasjonsform  varchar(5),
    naeringskode       varchar(5),
    formaal            varchar(100),
    firmanavn          varchar(150),
    telefon            varchar(22),
    epost              varchar(100),
    nettside           varchar(100),

    parent_org         integer references organisasjon (id)
);

create table adresse
(
    id                 integer primary key,
    organisasjon_id    integer references organisasjon (id),
    adressetype        varchar(5),
    adresselinje1      varchar(35),
    adresselinje2      varchar(35),
    adresselinje3      varchar(35),
    adresselinje4      varchar(35),
    adresselinje5      varchar(35),
    postnr             varchar(9),
    kommunenr          varchar(9),
    landkode           varchar(3)
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence organisasjon_seq;
create sequence adresse_seq;
