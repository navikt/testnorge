create sequence hibernate_sequence start with 1 increment by 1;
create table person
(
    id    bigint      not null primary key,
    updated timestamp not null,
    ident varchar(11) not null,
    body  json        not null
);