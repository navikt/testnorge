package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import static java.lang.Integer.parseInt;
import static no.nav.pdl.forvalter.domain.KjoennDTO.Kjoenn;
import static no.nav.pdl.forvalter.domain.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.pdl.forvalter.domain.KjoennDTO.Kjoenn.MANN;

@UtilityClass
public class KjoennFraIdentUtility {

    public Kjoenn getKjoenn(String ident) {

        var kjoennNummer = parseInt(ident.substring(8, 9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}