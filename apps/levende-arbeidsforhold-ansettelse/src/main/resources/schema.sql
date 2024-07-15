CREATE TABLE jobber (
    id VARCHAR(50) NOT NULL,
    aktiv BOOLEAN NOT NULL DEFAULT true,
    intervall CHAR(1) NOT NULL,
    antpersoner INTEGER NOT NULL,
    antbedrifter INTEGER NOT NULL
);