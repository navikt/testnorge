package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.identpool.domain.Identtype;

import static java.lang.Integer.parseInt;

@UtilityClass
public class IdenttypeFraIdentUtility {

    public static Identtype getIdenttype(String ident) {

        if ((parseInt(ident.substring(2, 4)) > 20 && parseInt(ident.substring(2, 4)) < 33) ||
                (parseInt(ident.substring(2, 4)) > 60 && parseInt(ident.substring(2, 4)) < 73)) {
            return Identtype.BOST;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return Identtype.DNR;
        } else {
            return Identtype.FNR;
        }
    }
}