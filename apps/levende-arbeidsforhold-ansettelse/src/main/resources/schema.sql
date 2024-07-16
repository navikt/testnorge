CREATE TABLE jobb_parameter (
    param_navn VARCHAR(50) NOT NULL UNIQUE,
    param_tekst VARCHAR(50) NOT NULL,
    param_verdi VARCHAR(255)
);