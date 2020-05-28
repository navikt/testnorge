create table kilde_system
(
    id         bigint              not null,
    navn       varchar(255) unique not null,
    created_at timestamp           not null,
    updated_at timestamp           not null,
    primary key (id)
);

alter table tps
    add column kilde_system_id bigint;

alter table tps
    add constraint kilde_system_id_fkey foreign key (kilde_system_id) references kilde_system;