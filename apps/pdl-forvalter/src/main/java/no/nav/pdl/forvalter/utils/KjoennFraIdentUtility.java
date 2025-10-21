package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;

import static java.lang.Integer.parseInt;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;

@UtilityClass
public class KjoennFraIdentUtility {

    public KjoennDTO.Kjoenn getKjoenn(String ident) {

        if (Id2032FraIdentUtility.isId2032(ident)) {
            return KjoennUtility.getKjoenn();
        }
        var kjoennNummer = parseInt(ident.substring(8, 9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}