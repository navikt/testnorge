-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
create table personidentifikator2032
(
    id                  bigserial   primary key,
    identtype           varchar(4)  not null,
    personidentifikator varchar(11) not null unique,
    dato_identifikator  varchar(6)  not null,
    individnummer       smallint    not null,
    allokert            boolean     not null,
    foedselsdato        date,
    dato_allokert       date,
    versjon             smallint    not null default 0
);

---------------------------------
-- C R E A T E   I N D E X E S --
---------------------------------
create unique index idx_personidentifikator_nytt_format
    on personidentifikator2032(personidentifikator);

create index idx_foedselsnummer_nytt_format
    on personidentifikator2032(dato_identifikator);

create index idx_individnummer_nytt_format
    on personidentifikator2032(individnummer);