-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

CREATE TABLE IF NOT EXISTS dokument
(
    id SERIAL PRIMARY KEY,
    bestilling_id BIGINT NOT NULL,
    dokument_type VARCHAR(30) NOT NULL,
    sist_oppdatert TIMESTAMP NOT NULL,
    versjon SMALLINT NOT NULL,
    contents TEXT NOT NULL
)