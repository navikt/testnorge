package no.nav.testnav.identpool.service;

import no.nav.testnav.identpool.domain.Identtype;
import org.springframework.stereotype.Component;

import static java.lang.Integer.parseInt;

@Component
public class IdenttypeFraIdentService {

    public Identtype getIdenttype(String ident) {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return Identtype.FDAT;
        } else if ((parseInt(ident.substring(2, 4)) > 20 && parseInt(ident.substring(2, 4)) < 33) ||
                (parseInt(ident.substring(2, 4)) > 60 && parseInt(ident.substring(2, 4)) < 73)) {
            return Identtype.BOST;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return Identtype.DNR;
        } else {
            return Identtype.FNR;
        }
    }
}