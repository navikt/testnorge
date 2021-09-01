-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table status(
    id                  integer primary key,
    organisasjonsnummer varchar(9),
    miljoe              varchar(4),
    uuid                varchar(50)
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence status_seq;
