-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table bestilling_mal
(
    id                  integer primary key,
    bestilling_id       integer references bestilling (id),
    mal_bestilling_navn varchar(100),
    opprettet_av_id     integer references bruker (id)

);

create table organisasjon_bestilling_mal
(
    id                  integer primary key,
    bestilling_id       integer references organisasjon_bestilling (id),
    mal_bestilling_navn varchar(100),
    opprettet_av_id     integer references bruker (id)
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence bestilling_mal_seq;
create sequence organisasjon_bestilling_mal_seq;
