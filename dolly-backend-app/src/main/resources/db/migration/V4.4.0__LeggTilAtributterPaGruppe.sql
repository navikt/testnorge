-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE t_gruppe
    ADD (
        er_laast VARCHAR2(1),
        laast_beskrivelse VARCHAR2(1000)
        );