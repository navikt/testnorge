package no.nav.identpool.ident.ajourhold.util;

import java.time.LocalDate;

public class PersonIdentifikatorUtil {

    private static final int[] CONTROL_DIGIT_C1 = { 3, 7, 6, 1, 8, 9, 4, 5, 2 };
    private static final int[] CONTROL_DIGIT_C2 = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };

    private PersonIdentifikatorUtil() {
    }

    public static LocalDate toBirthdate(String personIdentifikator) {
        int dag = Integer.parseInt(personIdentifikator.substring(0, 2));
        int maaned = Integer.parseInt(personIdentifikator.substring(2, 4));
        String aar = personIdentifikator.substring(4, 6);
        int epoke = Integer.parseInt(personIdentifikator.substring(6, 9));
        if (epoke > 0 && epoke < 500) {
            aar = "19" + aar;
        } else {
            aar = "20" + aar;
        }
        return LocalDate.of(Integer.parseInt(aar), maaned, dag);
    }

    public static boolean gyldigPersonidentifikator(String personIdentifikator) {
        try {
            Integer.parseInt(personIdentifikator);
            int digit = getControlDigit(personIdentifikator, CONTROL_DIGIT_C1);
            if (digit == 10 || digit != (personIdentifikator.charAt(9))) {
                return false;
            }
            digit = getControlDigit(personIdentifikator, CONTROL_DIGIT_C2);
            return digit != 10 && digit == personIdentifikator.charAt(10);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int getControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for (int i = 0; i < sequence.length; ++i) {
            digitsum += (fnr.charAt(i) - 48) * sequence[i];
        }
        digitsum = 11 - (digitsum % 11);
        return digitsum == 11 ? 0 : digitsum;
    }
}
