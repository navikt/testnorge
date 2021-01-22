package no.nav.identpool.util;

import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static no.nav.identpool.domain.Identtype.BOST;
import static no.nav.identpool.domain.Identtype.DNR;
import static no.nav.identpool.domain.Identtype.FDAT;
import static org.springframework.util.Assert.notNull;

import java.time.LocalDate;
import java.util.List;

import lombok.experimental.UtilityClass;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;

@UtilityClass
public final class PersonidentUtil {

    private static final int[] CONTROL_DIGIT_C1 = { 3, 7, 6, 1, 8, 9, 4, 5, 2 };
    private static final int[] CONTROL_DIGIT_C2 = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
    private static final int INVALID_CONTROL_DIGIT = 10;
    private static final int DEFAULT_MODULUS = 11;
    private static final int GENDER_POS = 8;
    private static final int C1_POS = 9;
    private static final int C2_POS = 10;

    public static void validate(String ident) {
        notNull(ident, "Personidentifikator kan ikke være null");

        if (ident.matches("\\d{11}")) {
            validateControlDigits(ident);
        } else {
            throw new UgyldigPersonidentifikatorException(String.format("%s inneholder ikke nok sifre", ident));
        }
    }

    public static void validateMultiple(List<String> identer) {

        identer.forEach(PersonidentUtil::validate);
    }

    public static Identtype getIdentType(String ident) {
        if (parseInt(ident.substring(0, 1)) > 3) {
            return DNR;
        } else if (parseInt(ident.substring(2, 3)) > 1) {
            return BOST;
        } else if ("0000".equals(ident.substring(6, 10))) {
            return FDAT;
        }
        return Identtype.FNR;
    }

    public static LocalDate toBirthdate(String ident) {
        int year = getFullYear(ident);
        int month = parseInt(ident.substring(2, 4));
        int day = parseInt(ident.substring(0, 2));

        if (DNR.equals(getIdentType(ident))) {
            day = day - 40;
        }
        if (BOST.equals(getIdentType(ident))) {
            month = month - 20;
        }

        return LocalDate.of(year, month, day);
    }

    public static String generateFnr(String birthdate) {
        int digit1 = calculateControlDigit(birthdate, CONTROL_DIGIT_C1);
        int digit2 = calculateControlDigit(birthdate + digit1, CONTROL_DIGIT_C2);

        return (digit1 == INVALID_CONTROL_DIGIT || digit2 == INVALID_CONTROL_DIGIT) ? null : birthdate + digit1 + digit2;
    }

    private static int calculateControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for (int i = 0; i < sequence.length; ++i) {
            digitsum += getNumericValue(fnr.charAt(i)) * sequence[i];
        }
        digitsum = DEFAULT_MODULUS - (digitsum % DEFAULT_MODULUS);

        return digitsum == DEFAULT_MODULUS ? 0 : digitsum;
    }

    /**
     * INDIVID(POS 7-9) 500-749 OG ÅR > 54 => ÅRHUNDRE = 1800
     * INDIVID(POS 7-9) 000-499            => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 900-999 OG ÅR > 39 => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 500-999 OG ÅR < 40 => ÅRHUNDRE = 2000
     */
    private static int getFullYear(String ident) {
        int year = parseInt(ident.substring(4, 6));
        int individ = parseInt(ident.substring(6, 9));

        // Find century
        int century;
        if (individ < 500 || (individ >= 900 && year > 39)) {
            century = 1900;
        } else if (year < 40) {
            century = 2000;
        } else if (individ < 750 && year > 54) {
            century = 1800;
        } else {
            century = 2000;
        }

        return LocalDate.of(century + year, 1, 1).getYear();
    }

    public static Kjoenn getKjonn(String fnr) {
        return (getNumericValue(fnr.charAt(GENDER_POS)) % 2 == 0) ? Kjoenn.KVINNE : Kjoenn.MANN;
    }

    private static void validateControlDigits(String ident) {
        int c1 = getNumericValue(ident.charAt(C1_POS));
        int c2 = getNumericValue(ident.charAt(C2_POS));
        int calcC1 = calculateControlDigit(ident, CONTROL_DIGIT_C1);
        int calcC2 = calculateControlDigit(ident, CONTROL_DIGIT_C2);

        if (calcC1 == INVALID_CONTROL_DIGIT || calcC2 == INVALID_CONTROL_DIGIT) {
            throw new UgyldigPersonidentifikatorException("Kontrollsiffer har ugyldig verdi (10)");
        }

        if (c1 != calcC1 || c2 != calcC2) {
            throw new UgyldigPersonidentifikatorException("Kontrollsiffer matcher ikke");
        }
    }

    public static boolean isSyntetisk(String ident) {
        return ident.charAt(2) > '3';
    }
}
