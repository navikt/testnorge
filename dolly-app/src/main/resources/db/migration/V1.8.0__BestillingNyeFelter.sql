-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BESTILLING
  ADD (
  OPPRETTET_FRA_ID NUMBER(9),
  TPSF_KRITERIER VARCHAR2(4000)
  );