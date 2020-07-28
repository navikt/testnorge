CREATE TABLE ident
(
    id        SERIAL PRIMARY KEY,
    ident     VARCHAR(256) unique NOT NULL,
    opprettet TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

create table hendelse
(
    id        SERIAL PRIMARY KEY,
    hendelse  VARCHAR(256) NOT NULL,
    fom       DATE    NOT NULL,
    tom       DATE,
    opprettet TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ident_id  INTEGER REFERENCES ident
);