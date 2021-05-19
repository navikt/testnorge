package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

import static java.lang.Integer.parseInt;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.KVINNE;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.MANN;

@UtilityClass
public class KjoennFraIdentUtility {

    private static final SecureRandom secureRandom = new SecureRandom();

    public Kjoenn get(String ident) {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return secureRandom.nextBoolean() ? MANN : KVINNE;
        }

        int kjoennNummer = parseInt(ident.substring(8, 9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}