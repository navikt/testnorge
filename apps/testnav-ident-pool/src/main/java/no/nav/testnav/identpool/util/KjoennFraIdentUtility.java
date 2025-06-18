package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.identpool.domain.Kjoenn;

import java.security.SecureRandom;

import static java.lang.Integer.parseInt;
import static no.nav.testnav.identpool.domain.Kjoenn.KVINNE;
import static no.nav.testnav.identpool.domain.Kjoenn.MANN;

@UtilityClass
public class KjoennFraIdentUtility {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static Kjoenn getKjoenn(String ident) {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return secureRandom.nextBoolean() ? MANN : KVINNE;
        }

        int kjoennNummer = parseInt(ident.substring(8, 9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}