package no.nav.dolly.libs.syntbean;

import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
class BeanValues {

    double toDouble(Object value) {

        if (isNull(value)) {
            return Double.NaN;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof Boolean bool) {
            return bool ? 1.0 : 0.0;
        }
        throw new IllegalStateException("Expected a numeric value, got " + value.getClass().getSimpleName());

    }

    long toLong(Object value) {
        return (long) toDouble(value);
    }

}
