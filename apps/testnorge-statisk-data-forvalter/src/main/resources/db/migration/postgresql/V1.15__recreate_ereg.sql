drop table ereg;

create table ereg
(
    orgnr                   varchar(255) not null,
    created_at              timestamp    not null,
    updated_at              timestamp    not null,
    enhetstype              varchar(255) not null,
    epost                   varchar(255),
    internet_adresse        varchar(255),
    naeringskode            varchar(255),
    navn                    varchar(255),
    parent                  varchar(255),
    forretnings_adresse     varchar(255),
    forretnings_kommunenr   varchar(255),
    forretnings_landkode    varchar(255),
    forretnings_postnr      varchar(255),
    forretnings_poststed    varchar(255),
    adresse                 varchar(255),
    kommunenr               varchar(255),
    landkode                varchar(255),
    postnr                  varchar(255),
    poststed                varchar(255),
    ekskludert              boolean default false,
    kilde_system_id         bigint,
    primary key (orgnr)
);

alter table ereg
    add constraint ereg_kilde_system_id_fkey foreign key (kilde_system_id) references kilde_system;

insert into ereg (orgnr, created_at, updated_at, enhetstype, epost, internet_adresse, naeringskode, navn, parent, forretnings_adresse, forretnings_kommunenr, forretnings_landkode, forretnings_postnr, forretnings_poststed, adresse, kommunenr, landkode, postnr, poststed, ekskludert, kilde_system_id)
select orgnr, created_at, updated_at, enhetstype, epost, internet_adresse, naeringskode, navn, parent, forretnings_adresse, forretnings_kommunenr, forretnings_landkode, forretnings_postnr, forretnings_poststed, adresse, kommunenr, landkode, postnr, poststed, ekskludert, kilde_system_id from ereg_backup;

alter table ereg
    add constraint ereg_parent_fkey foreign key (parent) references ereg;
