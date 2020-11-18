-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

create table bruker
(
    id         integer primary key,
    bruker_id  varchar(50) unique,
    brukernavn varchar(100),
    epost      varchar(100),
    nav_ident  varchar(10),
    eid_av_id  integer references bruker (id),
    migrert    boolean
);

create table gruppe
(
    id                integer primary key,
    navn              varchar(50) not null,
    hensikt           varchar(200) not null ,
    er_laast boolean,
    laast_beskrivelse varchar(1000),
    opprettet_av      integer not null references bruker (id),
    dato_endret       timestamp not null,
    sist_endret_av    integer not null references bruker (id)
);

create table bruker_favoritter
(
    bruker_id integer not null references bruker (id),
    gruppe_id integer not null references gruppe (id)
);

create table bestilling
(
    id                  integer primary key,
    sist_oppdatert      timestamp not null,
    gruppe_id           integer not null REFERENCES gruppe (id),
    antall_identer      smallint not null,
    bruker_id           integer REFERENCES bruker (id),
    miljoer             varchar(200) not null,
    tpsf_kriterier      varchar(4000),
    best_kriterier      text,
    ident               varchar(11),
    mal_bestilling_navn varchar(100),
    openam_sent         varchar(1500),
    opprettet_fra_id    integer,
    opprett_fra_identer varchar(4000),
    kilde_miljoe        varchar(10),
    tps_import          text,
    feil                varchar(1000),
    ferdig              boolean not null,
    stoppet             boolean not null
);

create table bestilling_kontroll
(
    id            integer primary key,
    bestilling_id integer not null references bestilling (id),
    stoppet       boolean not null
);

create table bestilling_progress
(
    id                        integer primary key,
    bestilling_id             integer not null references bestilling (id),
    ident                     varchar(11),
    aareg_status              varchar(4000),
    arenaforvalter_status     varchar(4000),
    bregstub_status           varchar(500),
    dokarkiv_status           varchar(500),
    feil                      varchar(4000),
    inntektsmelding_status    varchar(2000),
    inntektsstub_status       varchar(1000),
    instdata_status           varchar(4000),
    krrstub_status            varchar(4000),
    pdlforvalter_status       varchar(1000),
    pensjonforvalter_status   varchar(4000),
    sigrunstub_status         varchar(4000),
    skjermingsregister_status varchar(500),
    sykemelding_status        varchar(500),
    tpsf_success_environments varchar(4000),
    tps_import_status         varchar(500),
    udistub_status            varchar(500)
);

create table test_ident
(
    ident            varchar(11) primary key,
    tilhoerer_gruppe integer not null references gruppe (id),
    ibruk            boolean,
    beskrivelse      varchar(1000)
);

create table transaksjon_mapping
(
    id             integer primary key,
    bestilling_id  integer references bestilling (id),
    transaksjon_id varchar(300) not null,
    ident          varchar(11) not null,
    system         varchar(20) not null,
    miljoe         varchar(10),
    dato_endret    timestamp not null
);

-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence bestilling_kontroll_seq;
create sequence bestilling_progress_seq;
create sequence bestilling_seq;
create sequence bruker_seq;
create sequence gruppe_seq;
create sequence transaksjon_mapping_seq;