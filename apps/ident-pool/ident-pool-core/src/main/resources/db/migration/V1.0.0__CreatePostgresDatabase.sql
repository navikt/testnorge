-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table personidentifikator
(
    id                  integer primary key,
    identtype           varchar(5)         not null,
    personidentifikator varchar(13) unique not null,
    rekvireringsstatus  varchar(10)        not null,
    finnes_hos_skatt    boolean            not null,
    foedselsdato        timestamp          not null,
    kjoenn              varchar(8)         not null,
    rekvirert_av        varchar(30)
);

create table ajourhold
(
    id            integer primary key,
    status        varchar(15) not null,
    sistoppdatert timestamp,
    feilmelding   varchar(1023)
);

create table whitelist
(
    id  integer primary key,
    fnr varchar(255) unique
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence whitelist_seq start with 1;

create sequence ajourhold_seq start with 1;

create sequence personidentifikator_seq start with 1;
