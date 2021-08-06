package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class JavaTimeUtil {

    private JavaTimeUtil() {

    }

    public static String toString(LocalDate source) {
        return source != null ? source.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    public static String toString(LocalDateTime source) {
        return source != null ? source.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    public static String toString(YearMonth source) {
        return source != null ? source.format(DateTimeFormatter.ofPattern("yyyy-MM")) : null;
    }
}