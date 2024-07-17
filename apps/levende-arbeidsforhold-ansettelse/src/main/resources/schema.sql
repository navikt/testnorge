CREATE SCHEMA IF NOT EXISTS schema;
SET SCHEMA schema;
CREATE TABLE JOBB_PARAMETER (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PARAM_NAVN VARCHAR(255) NOT NULL,
    PARAM_TEKST VARCHAR(255) NOT NULL,
    PARAM_VERDI VARCHAR(255)
);

INSERT INTO JOBB_PARAMETER (PARAM_NAVN, PARAM_TEKST, PARAM_VERDI) VALUES ('antallOrganisasjoner', 'Organisasjoner', '100'),
                                                                         ('antallPersoner', 'Personer', '20'),
                                                                         ('typeArbeidsforhold', 'Type Arbeidsforhold', 'ordinaertArbeidsforhold'),
                                                                         ('arbeidstidsOrdning', 'Arbeidstids Ordning', 'ikkeSkift'),
                                                                         ('stillingsprosent', 'Stillingsprosent', '100.0');