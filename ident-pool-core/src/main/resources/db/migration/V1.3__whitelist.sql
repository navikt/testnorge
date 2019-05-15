create table WHITELIST
(
  FNR   varchar(11) primary key,
  WHITE boolean not null
);

ALTER TABLE WHITELIST
  ADD CONSTRAINT Unique_FNR UNIQUE (FNR);