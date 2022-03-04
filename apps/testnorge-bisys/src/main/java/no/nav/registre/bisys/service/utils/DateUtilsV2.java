package no.nav.registre.bisys.service.utils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class DateUtilsV2 {

    private DateUtilsV2() {
    }

    public static LocalDate getBirthdate(String fnr) {
        String birthdateStr = fnr.substring(0, 6);

        return LocalDate.parse(birthdateStr, DateTimeFormatter.ofPattern("ddMMyy"));
    }

    public static int getMonthsBetween(LocalDate firstDate, LocalDate secondDate){
        return Period.between(firstDate, secondDate).getMonths();
    }

    public static int getAgeInMonths(String fnr, LocalDate dateMeasured) {
        LocalDate fodselsdato = getBirthdate(fnr);
        return getMonthsBetween(fodselsdato.withDayOfMonth(1), dateMeasured.withDayOfMonth(0));
    }
}
