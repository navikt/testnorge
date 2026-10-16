package no.nav.dolly.synt.meldekort.generator;

import lombok.experimental.UtilityClass;

import java.util.Locale;

import static java.util.Objects.isNull;

@UtilityClass
class PythonText {

    private static final String NAN = "nan";

    static String lower(Object value) {
        return str(value).toLowerCase(Locale.ROOT);
    }

    static String upper(Object value) {
        return str(value).toUpperCase(Locale.ROOT);
    }

    static String str(Object value) {

        if (isNull(value)) {
            return NAN;
        }
        if (value instanceof Boolean bool) {
            return bool ? "True" : "False";
        }
        if (value instanceof Double number) {
            return floatText(number);
        }
        if (value instanceof Float number) {
            return floatText(number.doubleValue());
        }
        return value.toString();

    }

    static boolean not(Object value) {

        if (isNull(value)) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return !bool;
        }
        if (value instanceof String text) {
            return text.isEmpty();
        }
        if (value instanceof Number number) {
            return number.doubleValue() == 0.0;
        }
        return false;

    }

    static String number(double value) {

        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return Long.toString((long) value);
        }
        return floatText(value);

    }

    private static String floatText(double value) {

        if (Double.isNaN(value)) {
            return NAN;
        }
        if (Double.isInfinite(value)) {
            return value > 0 ? "inf" : "-inf";
        }
        return Double.toString(value);

    }

}
