-------------------------------
-- C R E A T E   T A B L E S --
-------------------------------
CREATE TABLE T_BESTILLING_KONTROLL (
  ID               NUMBER(9) PRIMARY KEY,
  BESTILLING_ID    NUMBER(9) NOT NULL,
  STOPPET          CHAR(1)   NOT NULL
);

---------------------------------------------------
-- F O R E I G N   K E Y   C O N S T R A I N T S --
---------------------------------------------------
ALTER TABLE T_BESTILLING_KONTROLL
  ADD CONSTRAINT BESTILLING_KONTROLL_FK FOREIGN KEY (BESTILLING_ID) REFERENCES T_BESTILLING (ID);

-----------------------
-- S E Q U E N C E S --
-----------------------
CREATE SEQUENCE T_BESTILLING_KONTROLL_SEQ START WITH 1;
