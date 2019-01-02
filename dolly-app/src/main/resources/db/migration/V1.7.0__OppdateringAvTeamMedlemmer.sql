-------------------------------
-- U P D A T E   T A B L E S --
-------------------------------
INSERT INTO T_TEAM_MEDLEMMER (TEAM_ID, BRUKER_ID)
SELECT ID, EIER
FROM T_TEAM
WHERE ID || '-' || EIER NOT IN
      (SELECT TEAM_ID || '-' || BRUKER_ID FROM T_TEAM_MEDLEMMER);

COMMIT;