package no.nav.registre.bisys.service.utils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Slf4j
@Service
public class DateUtils {

    private DateUtils() {
    }

    public static int getMonthsBetween(LocalDate firstDate, LocalDate secondDate) {
        return (int) ChronoUnit.MONTHS.between(firstDate, secondDate);
    }

    public static int getAgeInMonths(LocalDate foedselsdato, LocalDate dateMeasured) {
        return getMonthsBetween(foedselsdato.withDayOfMonth(1), dateMeasured.withDayOfMonth(1));
    }
}
