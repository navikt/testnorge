package no.nav.registre.orkestratoren.service.utils;

import java.time.LocalDate;

public class FnrUtility {

    public static LocalDate getFoedselsdatoFraFnr(String fnr) {
        int pnrAarhundreKode = Integer.parseInt(fnr.substring(6, 9));
        int day = Integer.parseInt(fnr.substring(0, 2));
        int month = Integer.parseInt(fnr.substring(2, 4));
        int year;

        if (pnrAarhundreKode < 500) {
            year = Integer.parseInt(19 + fnr.substring(4, 6));
        } else {
            year = Integer.parseInt(20 + fnr.substring(4, 6));
        }

        return LocalDate.of(year, month, day);
    }
}
