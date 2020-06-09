create sequence hibernate_sequence start with 1 increment by 1;
create table antall_timer_for_timeloennet
(
    id                                   bigint not null,
    antall_timer                         integer,
    antalltimerfortimeloennetperiode_fom varchar(255),
    antalltimerfortimeloennetperiode_tom varchar(255),
    arbeidsforhold_id                    bigint,
    primary key (id)
);
create table arbeidsavtale
(
    id                            bigint not null,
    antall_konverterte_timer      integer,
    arbeidstidsordning            varchar(255),
    avloenningstype               varchar(255),
    avtalt_arbeidstimer_per_uke   double precision,
    endringsdato_stillingsprosent varchar(255),
    stillingsprosent              double precision,
    yrke                          varchar(255),
    arbeidsforhold_id             bigint,
    primary key (id)
);
create table arbeidsforhold
(
    id                          bigint not null,
    ansettelsesperiode_fom      varchar(255),
    ansettelsesperiode_tom      varchar(255),
    arbeidsforholdid            varchar(255),
    arbeidsforholdidnav         integer,
    arbeidsforholdstype         varchar(255),
    arbeidsgiver_aktoertype     varchar(255),
    arbeidsgiver_orgnummer      varchar(255),
    arbeidstaker_aktoertype     varchar(255),
    arbeidstaker_ident          varchar(255),
    arbeidstaker_identtype      varchar(255),
    arbeidsforholds_response_id bigint,
    ident_fnr                   varchar(255),
    primary key (id)
);
create table arbeidsforholds_response
(
    id             bigint not null,
    arkivreferanse varchar(255),
    primary key (id)
);
create table ident
(
    fnr varchar(255) not null,
    primary key (fnr)
);
create table permisjon
(
    id                        bigint not null,
    permisjon_og_permittering varchar(255),
    permisjons_id             varchar(255),
    permisjonsperiode_fom     varchar(255),
    permisjonsperiode_tom     varchar(255),
    permisjonsprosent         double precision,
    arbeidsforhold_id         bigint,
    primary key (id)
);
create table utenlandsopphold
(
    id                          bigint not null,
    land                        varchar(255),
    utenlandsoppholdperiode_fom varchar(255),
    utenlandsoppholdperiode_tom varchar(255),
    arbeidsforhold_id           bigint,
    primary key (id)
);
alter table antall_timer_for_timeloennet
    add constraint FKehnx6exl7on45he1hb9vqu0u4 foreign key (arbeidsforhold_id) references arbeidsforhold;
alter table arbeidsavtale
    add constraint FK1qd2o8a54yy4q523ttk8j1tof foreign key (arbeidsforhold_id) references arbeidsforhold;
alter table arbeidsforhold
    add constraint FKkxbmv1ekpvenvpn48kmshtkcx foreign key (arbeidsforholds_response_id) references arbeidsforholds_response;
alter table arbeidsforhold
    add constraint FKssvb8o4teii1tje3sqq81n7hv foreign key (ident_fnr) references ident;
alter table permisjon
    add constraint FKt06pv6djl2g136wld6y3cl20i foreign key (arbeidsforhold_id) references arbeidsforhold;
alter table utenlandsopphold
    add constraint FK8wwuhxpb92qhvpqpk8s77by3d foreign key (arbeidsforhold_id) references arbeidsforhold;
