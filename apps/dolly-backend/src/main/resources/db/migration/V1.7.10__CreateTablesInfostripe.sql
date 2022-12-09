-----------------
-- T A B L E S --
-----------------

create table info_stripe
(
    id          integer         not null primary key,
    type        varchar(20)     not null,
    message     varchar(4000)   not null,
    start       timestamp       null,
    expires     timestamp       null
);

-----------------------
-- S E Q U E N C E S --
-----------------------
create sequence info_stripe_seq;
