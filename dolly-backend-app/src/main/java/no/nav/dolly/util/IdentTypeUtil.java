package no.nav.dolly.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.IdentType;

import static java.lang.Integer.parseInt;
import static no.nav.dolly.domain.resultset.IdentType.BOST;
import static no.nav.dolly.domain.resultset.IdentType.DNR;
import static no.nav.dolly.domain.resultset.IdentType.FDAT;
import static no.nav.dolly.domain.resultset.IdentType.FNR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentTypeUtil {

    public static IdentType getIdentType(String ident) {

        if (parseInt(String.valueOf(ident.charAt(0))) > 3) {
            return DNR;

        } else if (parseInt(String.valueOf(ident.charAt(2))) > 2) {
            return BOST;

        } else if ("000".equals(ident.substring(6, 9))) {
            return FDAT;

        } else {
            return FNR;
        }
    }
}
