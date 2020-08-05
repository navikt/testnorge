CREATE TABLE report
(
    id               SERIAL PRIMARY KEY,
    application_name VARCHAR(256) NOT NULL,
    name             VARCHAR(256) NOT NULL,
    run_start        TIMESTAMP    NOT NULL,
    run_end          TIMESTAMP    NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entry
(
    id          SERIAL PRIMARY KEY,
    status      VARCHAR(256) NOT NULL,
    description VARCHAR(256) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    report_id   INTEGER REFERENCES report
);