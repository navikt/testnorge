drop table hendelse;
drop table ident;

create table hendelse
(
    id        SERIAL PRIMARY KEY,
    hendelse  VARCHAR(256) NOT NULL,
    fom       DATE         NOT NULL,
    tom       DATE,
    opprettet TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ident     VARCHAR(256) NOT NULL
);

CREATE INDEX idx_ident ON hendelse (ident);