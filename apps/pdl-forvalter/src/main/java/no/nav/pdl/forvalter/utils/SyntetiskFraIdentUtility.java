package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SyntetiskFraIdentUtility {

    public boolean isSyntetisk(String ident) {

        return Integer.parseInt(String.valueOf(ident.charAt(2))) >= 4;
    }
}
