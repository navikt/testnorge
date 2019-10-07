package no.nav.registre.arena.domain.utils;

import java.util.Objects;

public class DateString {
    public static String dayMonthYearToYearMonthDay(String date) {
        if (Objects.isNull(date) || date.trim().isEmpty()) {
            return null;
        }
        String[] ddmmyyyy = date.split("-");
        if (ddmmyyyy.length == 3) {
            return (ddmmyyyy[2] + "-" + ddmmyyyy[1] + "-" + ddmmyyyy[0]);
        }
        return null;
    }
}
