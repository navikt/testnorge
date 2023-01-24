-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table tilgang
(
    id         integer primary key,
    orgnummer  varchar(50) unique,
    miljoe     varchar(100),
);


-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence tilgang_seq;