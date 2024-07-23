-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------


CREATE TABLE IF NOT EXISTS jobb_parameter (
                                //ID INT AUTO_INCREMENT PRIMARY KEY,
                                navn VARCHAR(255) NOT NULL PRIMARY KEY ,
                                tekst VARCHAR(255) NOT NULL,
                                verdi VARCHAR(255)
);

create table IF NOT EXISTS verdier (
    id INT SERIAL PRIMARY KEY ,
    verdi_navn varchar(255)  ,
    verdi_verdi varchar(255)  ,
    foreign key (verdi_navn) references jobb_parameter(navn)
);
/*
INSERT INTO JOBB_PARAMETER (NAVN, TEKST, VERDI) VALUES ('antallOrganisasjoner', 'Organisasjoner', '100'),
                                                                         ('antallPersoner', 'Personer', '20'),
                                                                         ('typeArbeidsforhold', 'Type Arbeidsforhold', 'ordinaertArbeidsforhold'),
                                                                         ('arbeidstidsOrdning', 'Arbeidstids Ordning', 'ikkeSkift'),
                                                                         ('stillingsprosent', 'Stillingsprosent', '100.0');

 */