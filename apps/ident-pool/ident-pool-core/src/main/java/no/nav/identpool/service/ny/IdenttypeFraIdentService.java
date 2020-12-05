package no.nav.identpool.service.ny;

import static java.lang.Integer.parseInt;
import static no.nav.identpool.domain.Identtype.BOST;
import static no.nav.identpool.domain.Identtype.DNR;
import static no.nav.identpool.domain.Identtype.FDAT;
import static no.nav.identpool.domain.Identtype.FNR;

import org.springframework.stereotype.Component;

import no.nav.identpool.domain.Identtype;

@Component
public class IdenttypeFraIdentService {

    public Identtype getIdenttype(String ident) {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return FDAT;
        } else if(parseInt(ident.substring(2, 3)) >= 2) {
            return BOST;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return DNR;
        } else {
            return FNR;
        }
    }
}