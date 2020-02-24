alter table aareg
    drop constraint FKpd5r2nnrk4n8vj9865roa5fu9;
alter table ereg
    drop constraint FK7ddr6tx72vo4bgc7i9hivkc4d;
alter table krr
    drop constraint FKgjnpjssc11ma80bndq8av8voe;
alter table tps
    drop constraint FKd0es5g5nruceok63u9bx0nq9q;

alter table aareg
    drop column team_id;
alter table ereg
    drop column team_id;
alter table krr
    drop column team_id;
alter table tps
    drop column team_id;

drop table team;

alter table aareg
    drop constraint FKg12x2u54f8nsibidajtmibrwj;
alter table ereg
    drop constraint FK17i3mfilg7cqov0igaan1ucm8;
alter table krr
    drop constraint FKo9ga91gm9qh9woe4o3x8u7uwn;
alter table tps
    drop constraint FKgd04hasrex6gg8x53um1jplel;

alter table aareg
    drop column varighet_id;
alter table ereg
    drop column varighet_id;
alter table krr
    drop column varighet_id;
alter table tps
    drop column varighet_id;

drop table varighet;