-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table bestilling_mal
(
    id                  integer primary key GENERATED ALWAYS AS IDENTITY,
    best_kriterier      text,
    miljoer             varchar(200),
    mal_bestilling_navn varchar(100),
    bruker_id           integer REFERENCES bruker (id),
    sist_oppdatert      timestamp default current_timestamp
);

create table organisasjon_bestilling_mal
(
    id                  integer primary key GENERATED ALWAYS AS IDENTITY,
    best_kriterier      text,
    miljoer             varchar(200),
    mal_bestilling_navn varchar(100),
    bruker_id           integer REFERENCES bruker (id),
    sist_oppdatert      timestamp default current_timestamp
);

-----------------------------------------
-- I N S E R T   I N T O   T A B L E S --
-----------------------------------------

insert into bestilling_mal (best_kriterier, miljoer, mal_bestilling_navn, bruker_id)
Select b.best_kriterier, b.miljoer, b.mal_bestilling_navn, b.bruker_id
from bestilling b
where b.mal_bestilling_navn is not null;

insert into organisasjon_bestilling_mal (best_kriterier, miljoer, mal_bestilling_navn, bruker_id)
Select b.best_kriterier, b.miljoer, b.mal_bestilling_navn, b.bruker_id
from organisasjon_bestilling b
where b.mal_bestilling_navn is not null;

commit;