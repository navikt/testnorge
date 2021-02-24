-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

alter table personidentifikator
alter column foedselsdato type date;

alter table ajourhold
alter column feilmelding type text;


-------------------------------------
-- C R E A T E   S E Q U E N C E S --
-------------------------------------
create sequence ajourhold_seq start with 263293;

create sequence personidentifikator_seq start with 13280165;
