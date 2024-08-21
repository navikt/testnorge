-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

CREATE TABLE IF NOT EXISTS jobb_parameter
(
    navn    VARCHAR(255) PRIMARY KEY,
    tekst   VARCHAR(255) NOT NULL,
    verdi   VARCHAR(255),
    verdier text
);

CREATE TABLE IF NOT EXISTS ansettelse_logg
(
    id                  INTEGER PRIMARY KEY,
    organisasjonsnummer VARCHAR(255) NOT NULL,
    folkeregisterident  VARCHAR(255) NOT NULL,
    timestamp           TIMESTAMP NOT NULL,
    ansattfra           DATE NOT NULL,
    arbeidsforhold_type VARCHAR(255),
    stillingsprosent    NUMERIC
);

-----------------------------------
-- C R E A T E   S E Q E N C E S --
-----------------------------------

CREATE SEQUENCE IF NOT EXISTS ansettelse_logg_id_seq;