create table team
(
    id          bigint       not null,
    epost       varchar(255) not null,
    navn        varchar(255) not null,
    slack_kanal varchar(255) not null,
    primary key (id)
);

alter table aareg
    add column team_id bigint;
alter table ereg
    add column team_id bigint;
alter table krr
    add column team_id bigint;
alter table tps
    add column team_id bigint;


alter table aareg
    add constraint FKpd5r2nnrk4n8vj9865roa5fu9 foreign key (team_id) references team;
alter table ereg
    add constraint FK7ddr6tx72vo4bgc7i9hivkc4d foreign key (team_id) references team;
alter table krr
    add constraint FKgjnpjssc11ma80bndq8av8voe foreign key (team_id) references team;
alter table tps
    add constraint FKd0es5g5nruceok63u9bx0nq9q foreign key (team_id) references team;
