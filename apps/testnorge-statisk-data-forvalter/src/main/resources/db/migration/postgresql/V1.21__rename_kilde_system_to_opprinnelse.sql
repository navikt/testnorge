alter table ereg
    drop constraint ereg_kilde_system_id_fkey;

alter table tps_identer
    rename column kilde_system_id to opprinnelse_id;

alter table ereg
    rename column kilde_system_id to opprinnelse_id;

create table opprinnelse as (select *
                             from kilde_system);
alter table opprinnelse
    alter column id set not null;
alter table opprinnelse
    alter column navn set not null;
alter table opprinnelse
    add primary key (id);
alter table opprinnelse
    add unique (navn);

alter table tps_identer
    add constraint tps_identer_opprinnelse_id_fkey foreign key (opprinnelse_id) references opprinnelse;

alter table ereg
    add constraint ereg_opprinnelse_id_fkey foreign key (opprinnelse_id) references opprinnelse;

drop table kilde_system;