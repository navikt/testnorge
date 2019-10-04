package no.nav.registre.arena.domain.utils;

public class DateString {
    public static String dayMonthYearToYearMonthDay(String date) {
        String[] ddmmyyyy = date.split("-");
        return (ddmmyyyy[2] + "-" + ddmmyyyy[1] + "-" + ddmmyyyy[0]);
    }
}
