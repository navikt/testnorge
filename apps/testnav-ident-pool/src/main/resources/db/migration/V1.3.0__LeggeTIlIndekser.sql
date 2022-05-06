---------------------------------
-- C R E A T E   I N D E X E S --
---------------------------------

create index idx_rekvireringsstatus_personidendifikator
on personidentifikator(rekvireringsstatus);

create index idx_foedselsdato_personidendifikator
    on personidentifikator(foedselsdato);

create index idx_kjoenn_personidendifikator
    on personidentifikator(kjoenn);

create index idx_identtype_personidendifikator
    on personidentifikator(identtype);