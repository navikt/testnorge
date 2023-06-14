-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table bestilling_mal
(
    id                  integer primary key,
    best_kriterier      text,
    miljoer             varchar(200),
    mal_bestilling_navn varchar(100),
    bruker_id           integer REFERENCES bruker (id),
    sist_oppdatert      timestamp
);

create table organisasjon_bestilling_mal
(
    id                  integer primary key,
    best_kriterier      text,
    miljoer             varchar(200),
    mal_bestilling_navn varchar(100),
    bruker_id           integer REFERENCES bruker (id),
    sist_oppdatert      timestamp
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence bestilling_mal_seq;
create sequence organisasjon_bestilling_mal_seq;
