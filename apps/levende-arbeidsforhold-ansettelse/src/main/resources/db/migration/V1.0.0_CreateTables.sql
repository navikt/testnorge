-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
DO
$$
BEGIN
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