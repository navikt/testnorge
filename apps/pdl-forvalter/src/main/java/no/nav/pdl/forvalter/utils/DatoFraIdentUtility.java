package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

import static java.lang.Integer.parseInt;

/**
 * INDIVID(POS 7-9) 500-749 OG AAR > 54 => AARHUNDRE = 1800
 * INDIVID(POS 7-9) 000-499            => AARHUNDRE = 1900
 * INDIVID(POS 7-9) 900-999 OG AAR > 39 => AARHUNDRE = 1900
 * INDIVID(POS 7-9) 500-999 OG AAR < 40 => AARHUNDRE = 2000
 */
@UtilityClass
public class DatoFraIdentUtility {

    public static LocalDate getDato(String ident) {

        var year = parseInt(ident.substring(4, 6));
        var individ = parseInt(ident.substring(6, 9));

        // Find century
        int century;
        if (parseInt(ident.substring(6, 10)) == 0) {
            century = year <= LocalDate.now().getYear() % 100 ? 2000 : 1900;
        } else if (individ < 500 || (individ >= 900 && year > 39)) {
            century = 1900;
        } else if (individ >= 500 && year < 40) {
            century = 2000;
        } else if (individ >= 500 && individ < 750 && year > 54) {
            century = 1800;
        } else {
            century = 2000;
        }

        return LocalDate.of(century + year, getMonth(ident), getDay(ident));
    }

    private int getDay(String ident) {
        // Fix D-number
        return ident.charAt(0) >= '4' ? parseInt(ident.substring(0, 2)) - 40 :
                parseInt(ident.substring(0, 2));
    }

    private int getMonth(String ident) {
        // Fix B-number and syntetisk
        if (ident.charAt(2) >= '6') {
            return parseInt(ident.substring(2, 4)) - 60;
        } else if (ident.charAt(2) >= '4') {
            return parseInt(ident.substring(2, 4)) - 40;
        } else {
            return parseInt(ident.substring(2, 4));
        }
    }
}