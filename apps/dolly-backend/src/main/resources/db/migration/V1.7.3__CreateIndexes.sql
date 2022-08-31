------------------------------
-- CREATE INDEX  --
------------------------------

CREATE INDEX IF NOT EXISTS bestilling_id_idx ON bestilling_progress ( bestilling_id DESC )
;

CREATE INDEX IF NOT EXISTS bestilling_id_idx ON bestilling_kontroll ( bestilling_id DESC )
;
