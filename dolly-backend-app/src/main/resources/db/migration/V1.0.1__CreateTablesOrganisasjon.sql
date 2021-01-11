-----------------
-- T A B L E S --
-----------------

create table organisasjon_bestilling
(
    id               integer      not null primary key,
    miljoer          varchar(200) not null,
    antall           integer      not null,
    sist_oppdatert   date         not null,
    ferdig           boolean,
    feil             varchar(1000),
    opprettet_fra_id integer,
    best_kriterier   text,
    bruker_id        integer references bruker (id)
);

create table organisasjon_bestilling_progress
(
    id                   integer primary key,
    bestilling_id        integer    not null references organisasjon_bestilling (id),
    organisasjonsnr      varchar(9) not null,
    org_forvalter_status varchar(2000)
);

create table organisasjon_nummer
(
    id              integer primary key,
    organisasjonsnr varchar(9) not null,
    bestilling_id   integer    not null references organisasjon_bestilling (id)
);

-----------------------
-- S E Q U E N C E S --
-----------------------
create sequence organisasjon_bestilling_seq;
create sequence organisasjon_bestilling_progress_seq;
create sequence organisasjon_nummer_seq;
