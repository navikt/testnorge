package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.domain.v1.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class JavaTimeUtil {

    private static final String YEAR_MONTH_PATTERN = "yyyy-MM";

    public static String toString(LocalDate date) {
        return date != null ? date.format(ISO_LOCAL_DATE) : null;
    }

    public static String toString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_LOCAL_DATE_TIME) : null;
    }

    public static String toString(YearMonth yearMonth) {
        return yearMonth != null ? yearMonth.format(DateTimeFormatter.ofPattern(YEAR_MONTH_PATTERN)) : null;
    }

    public static LocalDate toLocalDate(String date) {
        return date != null ? LocalDate.parse(date, ISO_LOCAL_DATE) : null;
    }

    public static LocalDateTime toLocalDateTime(String dateTime) {
        return dateTime != null ? LocalDateTime.parse(dateTime, ISO_LOCAL_DATE_TIME) : null;
    }

    public static YearMonth toYearMonth(String yearMonth) {
        return yearMonth != null ? YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern(YEAR_MONTH_PATTERN)) : null;
    }
}