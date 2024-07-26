-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
DO
$$
BEGIN
    -- TODO: Find a better way to do this
    --grant all privileges on all tables in schema public to cloudsqliamuser;
    grant all privileges on table jobb_parameter to cloudsqliamuser;
    grant all privileges on table verdier to cloudsqliamuser;
    grant all privileges on table jobb_parameter to "testnav-levende-arbeidsforhold-ansettelsev2";
    grant all privileges on table verdier to "testnav-levende-arbeidsforhold-ansettelsev2";
END
$$;


CREATE TABLE IF NOT EXISTS jobb_parameter (
                                navn VARCHAR(255) NOT NULL PRIMARY KEY ,
                                tekst VARCHAR(255) NOT NULL,
                                verdi VARCHAR(255),
                                verdier text[]
);

create table IF NOT EXISTS verdier (
    id SERIAL PRIMARY KEY ,
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