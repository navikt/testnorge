create table adresse
(
    id        bigint not null,
    adresse   varchar(255),
    kommunenr varchar(255),
    landkode  varchar(255),
    postnr    varchar(255),
    poststed  varchar(255),
    ereg_id   bigint not null,
    primary key (id)
);

alter table adresse
    add constraint FKs89vmb9w0g55bvgq5m8vw2leo foreign key (ereg_id) references ereg