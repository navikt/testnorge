create table varighet
(
    id          bigint    not null,
    created_at  timestamp not null,
    updated_at  timestamp not null,
    bestilt     date,
    ttl         varchar(255),
    varighet_id bigint,
    primary key (id)
);

alter table aareg
    add column varighet_id bigint;
alter table ereg
    add column varighet_id bigint;
alter table krr
    add column varighet_id bigint;
alter table tps
    add column varighet_id bigint;

alter table aareg
    add constraint FKg12x2u54f8nsibidajtmibrwj foreign key (varighet_id) references varighet;


alter table ereg
    add constraint FK17i3mfilg7cqov0igaan1ucm8 foreign key (varighet_id) references varighet;

alter table krr
    add constraint FKo9ga91gm9qh9woe4o3x8u7uwn foreign key (varighet_id) references varighet;

alter table tps
    add constraint FKgd04hasrex6gg8x53um1jplel foreign key (varighet_id) references varighet;