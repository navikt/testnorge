-----------------------------
-- A L T E R   T A B L E S --
-----------------------------

alter table status
    add column created_at timestamp default now();
