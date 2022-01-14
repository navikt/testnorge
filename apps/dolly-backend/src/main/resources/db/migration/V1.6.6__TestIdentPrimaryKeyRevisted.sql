------------------------------
-- A L T E R   T A B L E S  --
------------------------------

create table temp
(
    id               integer primary key,
    ident            varchar(11) unique,
    tilhoerer_gruppe integer not null references gruppe (id),
    ibruk            boolean,
    beskrivelse      varchar(1000),
    master           varchar(4)
);

create sequence test_ident_seq;

insert into temp(id, ident, tilhoerer_gruppe, ibruk, beskrivelse, master)
select nextval('test_ident_seq'), ti.ident, ti.tilhoerer_gruppe, ti.ibruk, ti.beskrivelse, ti.master from test_ident ti                                                           join bestilling_progress bp on bp.ident = ti.ident
    and bp.id = (select max(bps.id) from bestilling_progress bps where bps.ident = ti.ident)
order by bp.id asc;
commit;

drop table test_ident;

alter table temp
    rename to test_ident;