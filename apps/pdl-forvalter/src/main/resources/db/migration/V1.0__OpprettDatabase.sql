-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
create table person
(
    id             bigint      not null primary key,
    ident          varchar(11) not null unique,
    sist_oppdatert timestamp   not null,
    person         json        not null
);

create table relasjon
(
    id                 bigint      not null primary key,
    relasjon_type      varchar(30) not null,
    person_id          bigint      not null references person (id),
    relatert_person_id bigint      not null references person (id),
    sist_oppdatert     timestamp   not null
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence person_sequence start with 1 increment by 1;
create sequence relasjon_sequence start with 1 increment by 1;
