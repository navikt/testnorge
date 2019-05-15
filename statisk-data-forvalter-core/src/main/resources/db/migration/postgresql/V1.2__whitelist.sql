create table whitelist
(
  fnr   character varying not null,
  white boolean           not null,
  primary key (fnr)
);

create index idx_is_white on whitelist (white);
