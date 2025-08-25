-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------

alter table bestilling_kontroll
    alter column id set default nextval('bestilling_kontroll_seq');

alter table bruker
    alter column id set default nextval('bruker_seq');

alter table dokument
    alter column id set default nextval('dokument_id_seq');

alter table gruppe
    alter column id set default nextval('gruppe_seq');

alter table info_stripe
    alter column id set default nextval('info_stripe_seq');

alter table organisasjon_bestilling
    alter column id set default nextval('organisasjon_bestilling_seq');

alter table organisasjon_bestilling_progress
    alter column id set default nextval('organisasjon_bestilling_progress_seq');

alter table team
    alter column id set default nextval('team_id_seq');

alter table team_bruker
    alter column id set default nextval('team_bruker_id_seq');

alter table test_ident
    alter column id set default nextval('test_ident_seq');

alter table transaksjon_mapping
    alter column id set default nextval('transaksjon_mapping_seq');