package no.nav.identpool.service;

import no.nav.identpool.domain.Identtype;
import org.springframework.stereotype.Component;

import static java.lang.Integer.parseInt;
import static no.nav.identpool.domain.Identtype.BOST;
import static no.nav.identpool.domain.Identtype.DNR;
import static no.nav.identpool.domain.Identtype.FDAT;
import static no.nav.identpool.domain.Identtype.FNR;

@Component
public class IdenttypeFraIdentService {

    public Identtype getIdenttype(String ident) {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return FDAT;
        } else if ((parseInt(ident.substring(2, 4)) > 20 && parseInt(ident.substring(2, 4)) < 33) ||
                (parseInt(ident.substring(2, 4)) > 60 && parseInt(ident.substring(2, 4)) < 73)) {
            return BOST;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return DNR;
        } else {
            return FNR;
        }
    }
}