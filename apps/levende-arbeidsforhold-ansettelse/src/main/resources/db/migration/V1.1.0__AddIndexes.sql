-----------------------------------
-- C R E A T E   I N D E C I E S --
-----------------------------------

CREATE INDEX IF NOT EXISTS ansettelse_logg_ident
    ON ansettelse_logg (folkeregisterident);

CREATE INDEX IF NOT EXISTS ansettelse_logg_orgnummer
    ON ansettelse_logg (organisasjonsnummer);
