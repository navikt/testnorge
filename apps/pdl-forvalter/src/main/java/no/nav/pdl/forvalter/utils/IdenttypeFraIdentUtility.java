package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;

import static java.lang.Integer.parseInt;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.NPID;

@UtilityClass
public class IdenttypeFraIdentUtility {

    public static Identtype getIdenttype(String ident) {

        if (parseInt(ident.substring(2, 3)) % 4 >= 2) {
            return NPID;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return DNR;
        } else {
            return FNR;
        }
    }
}