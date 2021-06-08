CREATE TABLE inntektsmelding
(
    id         SERIAL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
create sequence hibernate_sequence start with 7000 increment by 1;