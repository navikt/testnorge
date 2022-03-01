package no.nav.registre.bisys.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DateUtils {

    private DateUtils() {
    }

    public static LocalDate getBirthdate(String fnr) {
        String birthdateStr = fnr.substring(0, 6);
        return LocalDate.parse(birthdateStr, DateTimeFormat.forPattern("ddMMyy"));
    }

    public static int getAgeInMonths(String fnr, LocalDate dateMeasured) {
        LocalDate fodselsdato = getBirthdate(fnr);

        return Months.monthsBetween(fodselsdato.dayOfMonth().withMinimumValue(), dateMeasured.dayOfMonth().withMinimumValue()).getMonths();
    }
}
