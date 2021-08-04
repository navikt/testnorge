package no.nav.testnav.libs.servletcore.util;

import java.util.concurrent.ThreadLocalRandom;

public class OrgnummerUtil {
    private static final String WEIGHTS = "32765432";

    private OrgnummerUtil() {
    }

    public static String generateOrgnr() {
        int random = ThreadLocalRandom.current().nextInt(80000000, 99999999);
        String randomString = String.valueOf(random);

        int controlDigit = calculateControlDigit(randomString);

        if (controlDigit < 0 || controlDigit > 9) {
            return generateOrgnr();
        }

        String orgnr = randomString + controlDigit;
        if (orgnr.length() != 9) {
            return generateOrgnr();
        }
        return orgnr;
    }

    public static int calculateControlDigit(String randomString) {
        int weightedSum = 0;
        for (int index = 0; index < randomString.length(); index++) {
            int vekt = Character.getNumericValue(WEIGHTS.charAt(index));
            int numericValue = Character.getNumericValue(randomString.charAt(index));

            weightedSum += (vekt * numericValue);
        }

        int rest = weightedSum % 11;
        if (rest == 0) {
            return 0;
        } else if (rest == 1) {
            //gir ugyldig controldigit
            return -1;
        }
        return 11 - rest;
    }
}
