package no.nav.registre.bisys.service.utils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
public class DateUtilsV2 {

    private static final int END_1900 = 499;
    private static final int START_1900 = 0;

    private DateUtilsV2() {
    }

    public static LocalDate getBirthdate(String fnr) {
        int day = parseInt(fnr.substring(0, 2));
        // Testnorge ident has plus 80 on month
        int month = parseInt(fnr.substring(2, 4)) - 80;

        String year = fnr.substring(4, 6);
        int periode = parseInt(fnr.substring(6, 9));
        String century = (periode >= START_1900 && periode <= END_1900) ? "19" : "20";

        return LocalDate.of(parseInt(century + year), month, day);
    }

    public static int getMonthsBetween(LocalDate firstDate, LocalDate secondDate){
        return (int) ChronoUnit.MONTHS.between(firstDate, secondDate);
    }

    public static int getAgeInMonths(String fnr, LocalDate dateMeasured) {
        LocalDate fodselsdato = getBirthdate(fnr);
        return getMonthsBetween(fodselsdato.withDayOfMonth(1), dateMeasured.withDayOfMonth(1));
    }
}
