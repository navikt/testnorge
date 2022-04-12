package no.nav.testnav.libs.servletcore.util;

import static java.lang.Integer.parseInt;

import java.time.LocalDate;

public class IdentUtil {

    private IdentUtil() {
        throw new IllegalStateException("IdentUtil class");
    }

    public static LocalDate getFoedselsdatoFraIdent(String ident) {
        var year = getFullYear(ident);
        var month = parseInt(ident.substring(2, 4));
        var day = parseInt(ident.substring(0, 2));

        if (isTestnorgeIdent(ident)) {
            month = month - 80;
        }

        var identtype = getIdentType(ident, month);
        if (Identtype.DNR.equals(identtype)) {
            day = day - 40;
        } else if (Identtype.BOST.equals(identtype)) {
            month = month - 20;
        }

        return LocalDate.of(year, month, day);
    }

    private static Identtype getIdentType(String ident, int month) {
        if (parseInt(ident.substring(0, 1)) > 3) {
            return Identtype.DNR;
        } else if (month > 12) {
            return Identtype.BOST;
        } else if ("0000".equals(ident.substring(6, 10))) {
            return Identtype.FDAT;
        }
        return Identtype.FNR;
    }

    private static boolean isTestnorgeIdent(String ident) {
        return parseInt(ident.substring(2, 3)) > 7;
    }

    /**
     * INDIVID(POS 7-9) 500-749 OG AAR > 54 => AARHUNDRE = 1800
     * INDIVID(POS 7-9) 000-499            => AARHUNDRE = 1900
     * INDIVID(POS 7-9) 900-999 OG AAR > 39 => AARHUNDRE = 1900
     * INDIVID(POS 7-9) 500-999 OG AAR < 40 => AARHUNDRE = 2000
     */
    private static int getFullYear(String ident) {
        var year = parseInt(ident.substring(4, 6));
        var individ = parseInt(ident.substring(6, 9));

        // Find century
        int century;
        if (individ < 500 || individ >= 900 && year > 39) {
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
}

