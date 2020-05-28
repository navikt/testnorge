create sequence hibernate_sequence start with 1 increment by 1;
create table ident
(
  id             bigint not null,
  dato_endret    timestamp,
  dato_opprettet timestamp,
  fnr            varchar(255),
  primary key (id)
);
