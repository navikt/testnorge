--------------------------
-- D R O P   T A B L E  --
--------------------------

drop table whitelist;


---------------------------------
-- M O D I F Y   T A B L E S   --
---------------------------------

alter table personidentifikator
drop
column finnes_hos_skatt;

alter table ajourhold
    alter column id set default nextval('ajourhold_seq');

alter table personidentifikator
    alter column id set default nextval('personidentifikator_seq');

alter table ajourhold
    add column melding text;

alter table ajourhold
    alter column status type varchar(20);