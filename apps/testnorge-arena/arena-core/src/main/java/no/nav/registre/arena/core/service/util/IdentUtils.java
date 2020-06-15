package no.nav.registre.arena.core.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static java.lang.Integer.parseInt;

@Service
@RequiredArgsConstructor
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
        int aar = parseInt(ident.substring(4, 6));
        int individ = parseInt(ident.substring(6, 9));

        // Find century
        int aarhundre;
        if (individ < 500 || (individ >= 900 && aar > 39)) {
            aarhundre = 1900;
        } else if (aar < 40) {
            aarhundre = 2000;
        } else if (individ < 750 && aar > 54) {
            aarhundre = 1800;
        } else {
            aarhundre = 2000;
        }

        return LocalDate.of(aarhundre + aar, 1, 1).getYear();
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
