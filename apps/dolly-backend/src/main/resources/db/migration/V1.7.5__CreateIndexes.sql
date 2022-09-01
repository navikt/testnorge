------------------------------
-- CREATE INDEX  --
------------------------------

CREATE INDEX IF NOT EXISTS bestilling_progress_ident_idx ON bestilling_progress ( ident DESC )
;

CREATE INDEX IF NOT EXISTS test_ident_tilhoerer_gruppe_idx ON test_ident ( tilhoerer_gruppe DESC )
;
