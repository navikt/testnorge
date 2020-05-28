create table gruppe
(
    id          bigint              not null,
    kode        varchar(255) unique not null,
    beskrivelse varchar(255)        not null,
    created_at  timestamp           not null,
    updated_at  timestamp           not null,
    primary key (kode)
);

alter table ereg
    add column gruppe_id varchar(255);

alter table ereg
    add constraint gruppe_gruppe_id_fkey foreign key (gruppe_id) references gruppe;