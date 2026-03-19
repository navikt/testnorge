package no.nav.organisasjonforvalter.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class JavaTimeUtil {

    public static String toString(LocalDate source) {
        return source != null ? source.format(ISO_LOCAL_DATE) : null;
    }

    public static String toString(LocalDateTime source) {
        return source != null ? source.format(ISO_LOCAL_DATE_TIME) : null;
    }
}