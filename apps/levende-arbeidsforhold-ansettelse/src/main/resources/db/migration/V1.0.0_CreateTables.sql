-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------

CREATE SCHEMA IF NOT EXISTS schema;
SET SCHEMA schema;
CREATE TABLE jobb_parameter (
                                ID INT AUTO_INCREMENT PRIMARY KEY,
                                NAVN VARCHAR(255) NOT NULL,
                                TEKST VARCHAR(255) NOT NULL,
                                VERDI VARCHAR(255)
);

/*
INSERT INTO JOBB_PARAMETER (NAVN, TEKST, VERDI) VALUES ('antallOrganisasjoner', 'Organisasjoner', '100'),
                                                                         ('antallPersoner', 'Personer', '20'),
                                                                         ('typeArbeidsforhold', 'Type Arbeidsforhold', 'ordinaertArbeidsforhold'),
                                                                         ('arbeidstidsOrdning', 'Arbeidstids Ordning', 'ikkeSkift'),
                                                                         ('stillingsprosent', 'Stillingsprosent', '100.0');

 */