package no.nav.pdl.forvalter.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SyntetiskFraIdentUtility {

    public static boolean isSyntetisk(String ident) {

        return Integer.parseInt(String.valueOf(ident.charAt(2))) >= 4;
    }
}
