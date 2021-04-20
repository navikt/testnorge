-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
create table person
(
    id      bigint      not null primary key,
    ident   varchar(11) not null unique,
    updated timestamp   not null,
    person  json        not null
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence person_sequence start with 1 increment by 1;
