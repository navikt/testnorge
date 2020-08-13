create table hent_rolle
(
    id    bigint       not null,
    orgnr bigint NOT NULL UNIQUE,
    json  text         NOT NULL,
    primary key (id)
);

CREATE INDEX ON hent_rolle (orgnr);