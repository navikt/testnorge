alter table tps_identer
    add column gruppe_id varchar(255);

alter table tps_identer
    add constraint tps_identer_gruppe_id_fkey foreign key (gruppe_id) references gruppe;

alter table aareg
    add column gruppe_id varchar(255);

alter table aareg
    add constraint aareg_gruppe_id_fkey foreign key (gruppe_id) references gruppe;

alter table krr
    add column gruppe_id varchar(255);

alter table krr
    add constraint krr_gruppe_id_fkey foreign key (gruppe_id) references gruppe;