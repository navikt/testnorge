package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.experimental.UtilityClass;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@UtilityClass
class PythonValues {

    private static final Pattern DECIMAL = Pattern.compile("[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?");
    private static final Pattern STRPTIME_ISO_DATE = Pattern.compile("(\\d{4})-(1[0-2]|0[1-9]|[1-9])-(3[01]|[12]\\d|0[1-9]|[1-9]| [1-9])");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter DIFFERENT_DATE = DateTimeFormatter.ofPattern("dd-MM-uuuu");
    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 9999;

    static double toFloat(Object value) {

        if (isNull(value)) {
            return Double.NaN;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        var text = value.toString().strip();
        if (DECIMAL.matcher(text).matches()) {
            return Double.parseDouble(text);
        }
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "nan", "+nan", "-nan" -> Double.NaN;
            case "inf", "+inf", "infinity", "+infinity" -> Double.POSITIVE_INFINITY;
            case "-inf", "-infinity" -> Double.NEGATIVE_INFINITY;
            default -> throw new NumberFormatException("could not convert string to float: '%s'".formatted(value));
        };

    }

    static double toFloatOrNaN(Object value) {

        try {
            return toFloat(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }

    }

    static long toInt(double value) {

        if (Double.isNaN(value)) {
            throw new NumberFormatException("cannot convert float NaN to integer");
        }
        if (Double.isInfinite(value)) {
            throw new ArithmeticException("cannot convert float infinity to integer");
        }
        return (long) value;

    }

    static boolean hasValue(Object value) {
        return !isNull(value) && !"".equals(value);
    }

    static LocalDate parseIsoDate(String value) {

        if (isNull(value)) {
            throw new IllegalArgumentException("Dato mangler");
        }
        var matcher = STRPTIME_ISO_DATE.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Dato '%s' har ikke formatet YYYY-MM-DD".formatted(value));
        }
        try {
            var date = LocalDate.of(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3).strip()));
            return validYear(date);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Dato '%s' er ugyldig".formatted(value), e);
        }

    }

    static LocalDate plusDays(LocalDate date, long days) {

        try {
            return validYear(date.plusDays(days));
        } catch (DateTimeException | ArithmeticException e) {
            throw new IllegalStateException("Dato %s pluss %d dager er utenfor gyldig område".formatted(date, days), e);
        }

    }

    static String formatIsoDate(LocalDate date) {
        return ISO_DATE.format(date);
    }

    static String formatDifferentDate(LocalDate date) {
        return DIFFERENT_DATE.format(date);
    }

    private static LocalDate validYear(LocalDate date) {

        if (date.getYear() < MIN_YEAR || date.getYear() > MAX_YEAR) {
            throw new DateTimeException("Year %d is out of range".formatted(date.getYear()));
        }
        return date;

    }

}
