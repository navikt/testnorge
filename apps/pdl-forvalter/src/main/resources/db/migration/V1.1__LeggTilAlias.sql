-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
create table alias
(
    id              bigint      not null primary key,
    sist_oppdatert  timestamp   not null,
    person_id       bigint      not null references person (id),
    tidligere_ident varchar(11) not null unique
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence alias_sequence start with 1 increment by 1;