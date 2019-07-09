create table ereg
(
    id               bigint       not null,
    created_at       timestamp    not null,
    updated_at       timestamp    not null,
    enhetstype       varchar(255) not null,
    epost            varchar(255),
    internet_adresse varchar(255),
    naeringskode     varchar(255),
    navn             varchar(255),
    orgnr            varchar(255) not null,
    primary key (id)
)