create sequence hibernate_sequence start with 1 increment by 1;
create table arbeidsforhold
(
  id                  bigint           not null,
  aarsak_ved_endring  varchar(255),
  arbeidforholds_id   varchar(255),
  beloep              double precision not null,
  foerste_fravaersdag date,
  primary key (id)
);
create table arbeidsgiver
(
  id                      bigint not null,
  kontaktinformasjon_navn varchar(255),
  telefonnummer           varchar(255),
  virksomhetsnummer       varchar(255),
  primary key (id)
);
create table delvis_fravaer
(
  id                               bigint           not null,
  dato                             date,
  timer                            double precision not null,
  omsorgspenger_delvis_fravaers_id bigint,
  primary key (id)
);
create table eier
(
  id   bigint not null,
  navn varchar(255),
  primary key (id)
);
create table gradering_i_foreldrepenger
(
  id                            bigint not null,
  gradering                     integer,
  periode_id                    bigint,
  gradering_i_foreldrepenger_id bigint,
  primary key (id)
);
create table inntektsmelding
(
  id                                                       bigint           not null,
  aarsak_til_innsending                                    varchar(255),
  arbeidstaker_fnr                                         varchar(255),
  avsendersystem_navn                                      varchar(255),
  avsendersystem_versjon                                   varchar(255),
  innsendingstidspunkt                                     timestamp,
  naer_relasjon                                            boolean          not null,
  omsorg_har_utbetalt_pliktige_dager                       boolean          not null,
  refusjonsbeloep_pr_mnd                                   double precision not null,
  refusjonsopphoersdato                                    date,
  startdato_foreldrepengeperiode                           date,
  sykepenger_begrunnelse_for_reduksjon_eller_ikke_utbetalt varchar(255),
  sykepenger_brutto_utbetalt                               double precision not null,
  ytelse                                                   varchar(255),
  arbeidsforhold_id                                        bigint,
  arbeidsgiver_id                                          bigint,
  eier_id                                                  bigint,
  privat_arbeidsgiver_id                                   bigint,
  inntektsmelding_id                                       bigint,
  primary key (id)
);
create table naturalytelse_detaljer
(
  id                              bigint           not null,
  beloep_pr_mnd                   double precision not null,
  fom                             date,
  type                            varchar(255),
  opphoer_av_naturalytelse_id     bigint,
  gjenopptakelse_naturalytelse_id bigint,
  primary key (id)
);
create table periode
(
  id                                bigint not null,
  fom                               date,
  tom                               date,
  sykepenger_periode_id             bigint,
  pleiepenger_periode_id            bigint,
  omsorgspenger_fravaers_periode_id bigint,
  avtalt_ferie_periode_id           bigint,
  primary key (id)
);
create table refusjons_endring
(
  id                     bigint           not null,
  endrings_dato          date,
  refusjonsbeloep_pr_mnd double precision not null,
  refusjon_endring_id    bigint,
  primary key (id)
);
create table utsettelse_av_foreldrepenger
(
  id                              bigint not null,
  aarsak_til_utsettelse           varchar(255),
  periode_id                      bigint,
  utsettelse_av_foreldrepenger_id bigint,
  primary key (id)
);
alter table arbeidsgiver
  add constraint UK_g28nsmh4sbr8rvakxjrpcq4cb unique (virksomhetsnummer);
alter table delvis_fravaer
  add constraint FKjsh73pspy3pyxq7up5tqo9rxb foreign key (omsorgspenger_delvis_fravaers_id) references inntektsmelding;
alter table gradering_i_foreldrepenger
  add constraint FKmrvy7hdyge6i6bi5xwnm0y5dn foreign key (periode_id) references periode;
alter table gradering_i_foreldrepenger
  add constraint FKdlschclixgxffp0nyapju05hb foreign key (gradering_i_foreldrepenger_id) references arbeidsforhold;
alter table inntektsmelding
  add constraint FKi5sdjx9kpv6kme2afhsr9xvqe foreign key (arbeidsforhold_id) references arbeidsforhold;
alter table inntektsmelding
  add constraint FKe74u2i0bwa2fxtxtsrssjiunv foreign key (arbeidsgiver_id) references arbeidsgiver;
alter table inntektsmelding
  add constraint FK6kfl8keo83r926g04wbxw18vu foreign key (eier_id) references eier;
alter table inntektsmelding
  add constraint FKarbi383l7f8c2hbfqk6e0vgrl foreign key (privat_arbeidsgiver_id) references arbeidsgiver;
alter table inntektsmelding
  add constraint FKtrptum3rifljvnfmd2ss0we6y foreign key (inntektsmelding_id) references eier;
alter table naturalytelse_detaljer
  add constraint FK280rv01jt5x3xbxrvovbeq7o7 foreign key (opphoer_av_naturalytelse_id) references inntektsmelding;
alter table naturalytelse_detaljer
  add constraint FK818v5ijlgv5tbg1c8pmdx5fxj foreign key (gjenopptakelse_naturalytelse_id) references inntektsmelding;
alter table periode
  add constraint FKelucalq675y71gkcac8c7jwqy foreign key (sykepenger_periode_id) references inntektsmelding;
alter table periode
  add constraint FKeyt13vtfblljuqnd4dncyexen foreign key (pleiepenger_periode_id) references inntektsmelding;
alter table periode
  add constraint FKfim32o7cc3pi3xcj9bf04x2h0 foreign key (omsorgspenger_fravaers_periode_id) references inntektsmelding;
alter table periode
  add constraint FK72vjyar5lgw5nc207omgby0ns foreign key (avtalt_ferie_periode_id) references arbeidsforhold;
alter table refusjons_endring
  add constraint FK71mw3c2nfabh58x2wkis958ij foreign key (refusjon_endring_id) references inntektsmelding;
alter table utsettelse_av_foreldrepenger
  add constraint FKqlhujsrjmw1k3dafpcks069ed foreign key (periode_id) references periode;
alter table utsettelse_av_foreldrepenger
  add constraint FKcldfydw18jlhk1col7vk2pmjh foreign key (utsettelse_av_foreldrepenger_id) references arbeidsforhold;
