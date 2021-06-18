package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.Identtype;

import static java.lang.Integer.parseInt;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.Identtype.BOST;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.Identtype.DNR;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.Identtype.FDAT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.Identtype.FNR;

@UtilityClass
public class IdenttypeFraIdentUtility {

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