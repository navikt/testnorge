package no.nav.identpool.ident.ajourhold.util;

import java.time.LocalDate;

public final class PersonIdentifikatorUtil {

    private static final int[] CONTROL_DIGIT_C1 = { 3, 7, 6, 1, 8, 9, 4, 5, 2 };
    private static final int[] CONTROL_DIGIT_C2 = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };

    private PersonIdentifikatorUtil() {
    }

    public static LocalDate toBirthdate(String personIdentifikator) {

        int dag = Integer.parseInt(personIdentifikator.substring(0, 2));

        if (Integer.parseInt(personIdentifikator.substring(0, 1)) > 3) {
            dag = Integer.parseInt(personIdentifikator.substring(0, 2)) - 40;
        }

        int maaned = Integer.parseInt(personIdentifikator.substring(2, 4));
        String aarstall = personIdentifikator.substring(4, 6);
        StringBuilder aar = new StringBuilder();
        int epoke = Integer.parseInt(personIdentifikator.substring(6, 9));
        if (epoke > 0 && epoke < 500) {
            aar.append(19).append(aarstall);
        } else {
            aar.append(20).append(aarstall);
        }
        return LocalDate.of(Integer.parseInt(aar.toString()), maaned, dag);
    }

    public static boolean gyldigPersonidentifikator(String personIdentifikator) {
        int digit = getControlDigit(personIdentifikator, CONTROL_DIGIT_C1);
        if (digit == 10 || digit != Character.getNumericValue(personIdentifikator.charAt(9))) {
            return false;
        }
        digit = getControlDigit(personIdentifikator, CONTROL_DIGIT_C2);

        return digit != 10 && digit == Character.getNumericValue(personIdentifikator.charAt(10));

    }

    private static int getControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for (int i = 0; i < sequence.length; ++i) {
            digitsum += Character.getNumericValue(fnr.charAt(i)) * sequence[i];
        }
        digitsum = 11 - (digitsum % 11);
        return digitsum == 11 ? 0 : digitsum;
    }
}
