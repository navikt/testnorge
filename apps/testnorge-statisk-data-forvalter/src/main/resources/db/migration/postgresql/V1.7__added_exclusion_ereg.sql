alter table ereg
    add column ekskludert boolean;
alter table ereg
    alter column ekskludert set default false;