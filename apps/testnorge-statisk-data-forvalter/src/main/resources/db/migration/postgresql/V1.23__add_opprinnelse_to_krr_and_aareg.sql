alter table aareg
    add column opprinnelse_id bigint;

alter table krr
    add column opprinnelse_id bigint;

alter table aareg
    add constraint aareg_opprinnelse_id_fkey foreign key (opprinnelse_id) references opprinnelse;

alter table krr
    add constraint krr_opprinnelse_id_fkey foreign key (opprinnelse_id) references opprinnelse;