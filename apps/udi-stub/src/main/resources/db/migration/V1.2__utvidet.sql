create table arbeidsadgang_utvidet
(
    id                 bigint not null,
    arbeids_omfang     integer,
    har_arbeids_adgang integer,
    fra                date,
    til                date,
    type_arbeidsadgang integer,
    forklaring         varchar(4000),
    hjemmel            varchar(4000),
    person_id          bigint not null,
    primary key (id)
);

