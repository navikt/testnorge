-----------------------------
-- M I G R A T E   D A T A --
-----------------------------

UPDATE t_bestilling
SET user_id = UPPER(user_id);

COMMIT;