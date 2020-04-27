create sequence hibernate_sequence start with 1 increment by 1;
create table rolleutskrift
(
    id    bigint       not null,
    ident varchar(255) NOT NULL UNIQUE,
    json  text         NOT NULL,
    primary key (id)
);

CREATE INDEX ON rolleutskrift (ident);