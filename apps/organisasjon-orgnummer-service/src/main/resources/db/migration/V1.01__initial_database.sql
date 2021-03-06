------------------------------
-- C R E A T E   T A B L E --
------------------------------

CREATE TABLE ORGNUMMER_POOL
(
    ID              SERIAL       PRIMARY KEY,
    ORGNUMMER       VARCHAR(9)   UNIQUE NOT NULL,
    LEDIG           BOOLEAN      NOT NULL,
    CREATED_AT      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);