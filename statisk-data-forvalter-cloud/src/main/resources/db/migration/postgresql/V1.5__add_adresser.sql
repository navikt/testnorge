create table adresse
(
    id        bigint not null,
    adresse   varchar(255),
    kommunenr varchar(255),
    landkode  varchar(255),
    postnr    varchar(255),
    poststed  varchar(255),
    primary key (id)
);

create table ereg_adresse
(
    adreses_id bigint not null,
    ereg_id    bigint not null,
    primary key (ereg_id)
);

alter table ereg_adresse
    add constraint UK_mwy9fnkebxg8f85ak183y14gu unique (adreses_id);
alter table ereg_adresse
    add constraint FKsc5tbhryssbqy7xeuik532naj foreign key (adreses_id) references ereg;
alter table ereg_adresse
    add constraint FKfm1k7o3ad0mk31vl67786ta2o foreign key (ereg_id) references adresse;