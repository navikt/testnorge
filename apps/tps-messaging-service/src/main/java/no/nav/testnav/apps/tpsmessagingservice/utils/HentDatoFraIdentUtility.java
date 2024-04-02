package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;

/**
 * INDIVID(POS 7-9) 500-749 OG ÅR > 54 => ÅRHUNDRE = 1800
 * INDIVID(POS 7-9) 000-499            => ÅRHUNDRE = 1900
 * INDIVID(POS 7-9) 900-999 OG ÅR > 39 => ÅRHUNDRE = 1900
 * INDIVID(POS 7-9) 500-999 OG ÅR < 40 => ÅRHUNDRE = 2000
 */
@UtilityClass
public class HentDatoFraIdentUtility {

    private static final LocalDateTime TPS_MIN_REG_DATE = of(1900, 1, 1, 0, 0);

    public static LocalDateTime extract(String ident) {

        int year = parseInt(ident.substring(4, 6));
        int individ = parseInt(ident.substring(6, 9));

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

        return LocalDateTime.of(century + year, getMonth(ident), getDay(ident), 0, 0);
    }

    public static LocalDateTime enforceValidTpsDate(LocalDateTime date) {

        if (isNull(date)) {
            return null;
        } else {
            return date.isBefore(TPS_MIN_REG_DATE) ? TPS_MIN_REG_DATE : date;
        }
    }

    private int getDay(String ident) {

        return parseInt(ident.substring(0, 2)) % 40;
    }

    private int getMonth(String ident) {

        return parseInt(ident.substring(2, 4)) % 20;
    }
}