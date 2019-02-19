-----------------------------
-- A L T E R   T A B L E S --
-----------------------------
ALTER TABLE T_BESTILLING
  MODIFY (
  ANTALL_IDENTER NUMBER(5) NULL
  );

ALTER TABLE T_BESTILLING
  ADD (
  EKSISTERENDE_IDENTER VARCHAR2(4000)
  );