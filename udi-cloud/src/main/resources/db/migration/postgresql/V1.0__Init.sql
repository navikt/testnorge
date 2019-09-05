create sequence hibernate_sequence start with 1 increment by 1;
create table aliaser
(
    id         bigint not null,
    fnr        varchar(255),
    etternavn  varchar(255),
    fornavn    varchar(255),
    mellomnavn varchar(255),
    person_id  bigint not null,
    primary key (id)
);
create table arbeidsadgang
(
    id                 bigint not null,
    arbeids_omfang     integer,
    har_arbeids_adgang integer,
    fra                date,
    til                date,
    type_arbeidsadgang integer,
    person_id          bigint not null,
    primary key (id)
);
create table avgjoerelse
(
    id                            bigint  not null,
    avgjoerelses_dato             date,
    effektuerings_dato            date,
    er_positiv                    boolean not null,
    etat                          varchar(255),
    grunntype_kode_kode           varchar(255),
    grunntype_kode_type           varchar(255),
    grunntype_kode_visningsnavn   varchar(255),
    har_flyktningstatus           boolean not null,
    iverksettelse_dato            date,
    omgjort_avgjoerelses_id       varchar(255),
    saksnummer                    varchar(255),
    tillatelse_kode_kode          varchar(255),
    tillatelse_kode_type          varchar(255),
    tillatelse_kode_visningsnavn  varchar(255),
    tillatelse_fra                date,
    tillatelse_til                date,
    tillatelse_varighet           integer,
    tillatelse_kode               varchar(255),
    tillatelse_type               varchar(255),
    tillatelse_visningsnavn       varchar(255),
    uavklart_flyktningstatus      boolean not null,
    utfall_fra                    date,
    utfall_til                    date,
    utfall_varighet               integer,
    utfall_kode                   varchar(255),
    utfall_type                   varchar(255),
    utfall_visningsnavn           varchar(255),
    utfallstype_kode_kode         varchar(255),
    utfallstype_kode_type         varchar(255),
    utfallstype_kode_visningsnavn varchar(255),
    utreisefrist_dato             date,
    person_id                     bigint  not null,
    primary key (id)
);
create table opphold_status
(
    id                                                     bigint not null,
    eos_ellereftabeslutning_om_oppholdsrett                integer,
    eos_ellereftabeslutning_om_oppholdsrett_effektuering   date,
    efta_beslutning_om_oppholdsrett_fra                    date,
    efta_beslutning_om_oppholdsrett_oppholds_til           date,
    eos_ellereftaoppholdstillatelse                        integer,
    eos_ellereftaoppholdstillatelse_effektuering           date,
    efta_oppholdstillatelse_fra                            date,
    efta_oppholdstillatelse_til                            date,
    eos_ellereftavedtak_om_varig_oppholdsrett              integer,
    eos_ellereftavedtak_om_varig_oppholdsrett_effektuering date,
    efta_vedtak_om_varig_oppholdsrett_fra                  date,
    efta_vedtak_om_varig_oppholdsrett_til                  date,
    avgjorelses_dato                                       date,
    avslag_grunnlag_overig                                 integer,
    avslag_grunnlag_tillatelse_grunnlageos                 integer,
    avslag_oppholdsrett_behandlet                          integer,
    avslag_oppholdstillatelse_behandlet_grunnlageos        integer,
    avslag_oppholdstillatelse_behandlet_grunnlag_ovrig     integer,
    avslag_oppholdstillatelse_behandlet_utreise_frist      date,
    avslag_oppholdstillatelse_utreise_frist                date,
    bortfall_avpoellerbosdato                              date,
    formelt_vedtak_utreise_frist                           date,
    tilbake_kall_utreise_frist                             date,
    tilbake_kall_virknings_dato                            date,
    ovrig_ikke_oppholds_kategori_arsak                     integer,
    innreise_forbud                                        integer,
    innreise_forbud_vedtaks_dato                           date,
    varighet                                               integer,
    opphold_samme_vilkaar_effektuering                     date,
    opphold_samme_vilkaar_fra                              date,
    opphold_samme_vilkaar_til                              date,
    oppholdstillatelse_type                                integer,
    oppholdstillatelse_vedtaks_dato                        date,
    uavklart                                               boolean,
    person_id                                              bigint not null,
    primary key (id)
);
create table person
(
    id                                      bigint       not null,
    avgjoerelse_uavklart                    boolean,
    flyktning                               boolean,
    foedsels_dato                           date,
    har_oppholds_tillatelse                 boolean,
    ident                                   varchar(255) not null,
    etternavn                               varchar(255),
    fornavn                                 varchar(255),
    mellomnavn                              varchar(255),
    soeknad_om_beskyttelse_under_behandling integer,
    soknad_dato                             date,
    primary key (id)
);
alter table arbeidsadgang
    add constraint UK_5ruw5d5xqg8pglx263dimx7d7 unique (person_id);
alter table opphold_status
    add constraint UK_d7sidhayn98rxyldrdrqbnams unique (person_id);
alter table person
    add constraint UK_qaceom4e7dg91eh1o8rcx2run unique (ident);
alter table aliaser
    add constraint FK1uo8ixmnm000bqeihfdy5ng5p foreign key (person_id) references person;
alter table arbeidsadgang
    add constraint FK2poj0jps16e9m1eqp1oi9jer3 foreign key (person_id) references person;
alter table avgjoerelse
    add constraint FKdtj6snyeigm1vbgfqteo53bnq foreign key (person_id) references person;
alter table opphold_status
    add constraint FKsb46tp039h1wfi2tuuhqt0tt1 foreign key (person_id) references person;
