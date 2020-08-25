CREATE TABLE inntektsmelding
(
    id         SERIAL PRIMARY KEY,
    ident      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX ON inntektsmelding (ident);