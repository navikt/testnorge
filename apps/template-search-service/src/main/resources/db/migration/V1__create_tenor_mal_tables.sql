create table tenor_person_mal
(
    id                    bigint generated always as identity primary key,
    mal_navn              varchar(100)             not null,
    soek_kriterier         text                     not null,
    bruker_id              varchar(100)             not null,
    brukernavn             varchar(100)             not null,
    brukertype             varchar(10)              not null,
    opprettet              timestamp with time zone not null default current_timestamp,
    sist_oppdatert         timestamp with time zone not null default current_timestamp
);

create index tenor_person_mal_type_user_idx on tenor_person_mal (brukertype, bruker_id);
create unique index tenor_person_mal_bruker_navn_idx
    on tenor_person_mal (bruker_id, ${malNameIndexExpression});
