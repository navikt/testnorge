---------------------------------------------------
-- D E L E T E   O B S O L E T E   E N T R I E S --
---------------------------------------------------

delete
from bestilling_progress bp
where id in (
    select bp.id
    from bestilling_progress bp
             join bestilling b on b.id = bp.bestilling_id
             join test_ident ti on ti.ident = bp.ident
        and ti.tilhoerer_gruppe <> b.gruppe_id);

commit;