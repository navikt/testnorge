-----------------------------
-- A L T E R   T A B L E S --
-----------------------------

CREATE TABLE IF NOT EXISTS dokument
(
    id SERIAL PRIMARY KEY,
    sist_oppdatert TIMESTAMP,
    versjon SMALLINT,
    contents TEXT
)

