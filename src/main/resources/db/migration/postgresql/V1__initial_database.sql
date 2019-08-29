create table aareg
(
    fnr        varchar(255) not null,
    created_at timestamp    not null,
    updated_at timestamp    not null,
    org_id     bigint       not null,
    primary key (fnr)
);
create table dkif
(
    fnr         varchar(255) not null,
    created_at  timestamp    not null,
    updated_at  timestamp    not null,
    email       varchar(255),
    email_valid boolean      not null,
    name        varchar(255),
    reserved    boolean      not null,
    sdp         boolean      not null,
    sms         varchar(255),
    sms_valid   boolean      not null,
    primary key (fnr)
);
create table tps
(
    fnr        varchar(255) not null,
    created_at timestamp    not null,
    updated_at timestamp    not null,
    address    varchar(255),
    city       varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    post_nr    varchar(255),
    primary key (fnr)
);
