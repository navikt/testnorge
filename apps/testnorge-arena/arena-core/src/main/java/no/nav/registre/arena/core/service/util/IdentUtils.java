package no.nav.registre.arena.core.service.util;

import java.time.LocalDate;

import static java.lang.Integer.parseInt;

public class IdentUtils {

    private static final String DNR = "DNR";
    private static final String BOST = "BOST";
    private static final String FNR = "FNR";
    private static final String FDAT = "FDAT";

    public static LocalDate hentFoedseldato(String ident) {
        int aar = getFullYear(ident);
        int maaned = parseInt(ident.substring(2, 4));
        int dag = parseInt(ident.substring(0, 2));

        if (DNR.equals(getIdentType(ident))) {
            dag = dag - 40;
        }
        if (BOST.equals(getIdentType(ident))) {
            maaned = maaned - 20;
        }

        return LocalDate.of(aar, maaned, dag);
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

    public static String getIdentType(String ident) {
        if (parseInt(ident.substring(0, 1)) > 3) {
            return DNR;
        } else if (parseInt(ident.substring(2, 3)) > 1) {
            return BOST;
        } else if ("0000".equals(ident.substring(6, 10))) {
            return FDAT;
        }
        return FNR;
    }
}
