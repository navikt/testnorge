create table kodeverk
(
    id           bigint not null,
    aktiv_fom    date,
    aktiv_tom    date,
    kode         varchar(255),
    type         varchar(255),
    visningsnavn varchar(255),
    primary key (id)
);

alter table avgjoerelse
    drop column grunntype_kode_kode,
    drop column grunntype_kode_type,
    drop column grunntype_kode_visningsnavn,
    drop column tillatelse_kode_kode,
    drop column tillatelse_kode_type,
    drop column tillatelse_kode_visningsnavn,
    drop column tillatelse_kode,
    drop column tillatelse_type,
    drop column tillatelse_visningsnavn,
    drop column utfall_kode,
    drop column utfall_type,
    drop column utfall_visningsnavn,
    drop column utfallstype_kode_kode,
    drop column utfallstype_kode_type,
    drop column utfallstype_kode_visningsnavn;

alter table avgjoerelse
    add column grunntype_kode_id           bigint,
    add column tillatelse_kode_id          bigint,
    add column tillatelse_varighet_kode_id bigint,
    add column utfall_varighet_kode_id     bigint,
    add column utfallstype_kode_id         bigint;

alter table avgjoerelse
    add constraint FKkt327qo58o60lbh9w6op920or foreign key (grunntype_kode_id) references kodeverk;
alter table avgjoerelse
    add constraint FK21odw98rqieosj15qxifaud86 foreign key (tillatelse_kode_id) references kodeverk;
alter table avgjoerelse
    add constraint FKj2h049g08t7a9dpcf9moowlsx foreign key (tillatelse_varighet_kode_id) references kodeverk;
alter table avgjoerelse
    add constraint FKsc3edt7yykl5lnctckqj36ni6 foreign key (utfall_varighet_kode_id) references kodeverk;
alter table avgjoerelse
    add constraint FK5lxfob4bi7u2fxr1ky6kbstsw foreign key (utfallstype_kode_id) references kodeverk;

