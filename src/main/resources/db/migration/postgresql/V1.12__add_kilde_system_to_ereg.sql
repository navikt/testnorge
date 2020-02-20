alter table ereg
    add column kilde_system_id bigint;

alter table ereg
    add constraint ereg_kilde_system_id_fkey foreign key (kilde_system_id) references kilde_system;