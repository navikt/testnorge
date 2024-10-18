-----------------------------------
-- M O D I F Y   I N D E C I E S --
-----------------------------------

ALTER TABLE ansettelse_logg
ALTER COLUMN id SET DEFAULT NEXTVAL('ansettelse_logg_id_seq');
