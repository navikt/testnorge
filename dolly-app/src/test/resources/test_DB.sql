--SQL kommandoer for manuell testing av relasjonene mellom de ulike tabellene i databasen.
--Disse kan brukes for å teste at jpa og flyway-skript samkjører.
INSERT INTO T_BRUKER VALUES ('S222222');
INSERT INTO T_BRUKER (NAV_IDENT) VALUES ('S111111');
INSERT INTO T_TEAM (ID,NAVN,BESKRIVELSE,DATO_OPPRETTET,EIER)VALUES (1, 'teamdollyNAVN', 'dollyDB testes', {ts '2018-04-12 18:47:52.00'},  'S222222');
INSERT INTO TEAM_MEDLEMMER (TEAM_ID,BRUKER_ID) VALUES (1, 'S111111');
INSERT INTO TEAM_MEDLEMMER (TEAM_ID,BRUKER_ID) VALUES (1, 'S222222');
--TEAM og BRUKERs eierskapsforhold
SELECT * FROM TEAM_MEDLEMMER;

SELECT *
FROM T_BRUKER
  JOIN
  T_TEAM ON T_TEAM.EIER = T_BRUKER.NAV_IDENT
WHERE T_BRUKER.NAV_IDENT = 'S222222';
SELECT *
FROM T_TEAM
  JOIN T_BRUKER ON T_BRUKER.NAV_IDENT = T_TEAM.EIER
WHERE T_TEAM.EIER = 'S222222';

--Team og brukers medlemskapsforhold (many-to-many relasjon)
SELECT *
FROM T_BRUKER
  JOIN TEAM_MEDLEMMER ON TEAM_MEDLEMMER.BRUKER_ID = T_BRUKER.NAV_IDENT
  JOIN T_TEAM ON T_TEAM.ID = TEAM_MEDLEMMER.TEAM_ID
WHERE T_BRUKER.NAV_IDENT = 'S111111';


INSERT INTO T_TESTGRUPPE ( ID, NAVN, OPPRETTET_AV, DATO_ENDRET, SIST_ENDRET_AV, TILHOERER_TEAM )
                  VALUES (1, 'dolly testgruppe1', 'S222222', {ts '2018-04-13 10:47:52.00'},  'S111111', 1);

--Testgruppe opprettet av bruker
SELECT *
FROM T_TESTGRUPPE
  JOIN T_BRUKER ON T_TESTGRUPPE.OPPRETTET_AV = T_BRUKER.NAV_IDENT
WHERE T_TESTGRUPPE.OPPRETTET_AV = 'S222222';
--Bruker har opprettet testgruppe
SELECT *
FROM T_BRUKER
  JOIN T_TESTGRUPPE ON T_TESTGRUPPE.OPPRETTET_AV = T_BRUKER.NAV_IDENT
WHERE T_BRUKER.NAV_IDENT = 'S222222';

--Testidenter
INSERT INTO T_TEST_IDENT VALUES (11111111111, 1);
INSERT INTO T_TEST_IDENT (IDENT, TILHOERER_GRUPPE) VALUES (22222222222, 1);

SELECT *
FROM T_TEST_IDENT
  JOIN T_TESTGRUPPE ON T_TESTGRUPPE.ID = T_TEST_IDENT.TILHOERER_GRUPPE
WHERE T_TESTGRUPPE.ID = 1;