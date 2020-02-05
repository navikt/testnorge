-----------------------------
-- A L T E R   T A B L E S --
-----------------------------

ALTER TABLE T_GRUPPE
    DROP COLUMN OPENAM_SENT;

ALTER TABLE T_GRUPPE
    DROP CONSTRAINT GRUPPE_TEAM_FK;

ALTER TABLE T_GRUPPE
    DROP COLUMN TILHOERER_TEAM;


---------------------------
-- D R O P   T A B L E S --
---------------------------

DROP TABLE T_TEAM_MEDLEMMER;

DROP TABLE T_TEAM;

