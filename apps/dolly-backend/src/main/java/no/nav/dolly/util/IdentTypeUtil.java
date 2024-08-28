package no.nav.dolly.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.IdentType;

import static java.lang.Integer.parseInt;
import static no.nav.dolly.domain.resultset.IdentType.DNR;
import static no.nav.dolly.domain.resultset.IdentType.FNR;
import static no.nav.dolly.domain.resultset.IdentType.NPID;
import static org.apache.poi.util.StringUtil.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentTypeUtil {

    public static IdentType getIdentType(String ident) {

        if (parseInt(String.valueOf(ident.charAt(0))) > 3) {
            return DNR;

        } else if (parseInt(String.valueOf(ident.charAt(2))) % 4 >= 2) {
            return NPID;

        } else {
            return FNR;
        }
    }

    public static boolean isSyntetisk(String ident) {

        return isNotBlank(ident) && ident.length() == 11 &&
                Integer.parseInt(ident.substring(2, 3)) >= 4;
    }

    public static boolean isDollyIdent(String ident) {

        return isSyntetisk(ident) &&
                ident.substring(2, 3).matches("[4,5]");
    }

    public static boolean isTenorIdent(String ident) {

        return isSyntetisk(ident) &&
                ident.substring(2, 3).matches("[8,9]");
    }
}
