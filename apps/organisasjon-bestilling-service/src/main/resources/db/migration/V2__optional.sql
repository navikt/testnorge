ALTER TABLE ORGANISASJON_ORDER
    ALTER COLUMN BATCH_ID DROP NOT NULL;

ALTER TABLE ORGANISASJON_ORDER
    ALTER COLUMN ENVIRONMENT DROP NOT NULL;

ALTER TABLE ORGANISASJON_ORDER
    ADD UPDATED_AT TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;