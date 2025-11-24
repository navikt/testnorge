-------------------------------
-- M O D I F Y   T A B L E S --
-------------------------------

ALTER TABLE test_ident
ALTER COLUMN ibruk
SET DEFAULT false;

UPDATE test_ident
SET ibruk = false
WHERE ibruk is null;

commit;