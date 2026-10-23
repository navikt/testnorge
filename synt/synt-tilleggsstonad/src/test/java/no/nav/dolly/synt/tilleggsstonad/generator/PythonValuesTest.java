package no.nav.dolly.synt.tilleggsstonad.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PythonValuesTest {

    @ParameterizedTest
    @CsvSource({
            "152, 152.0",
            "' 3.5 ', 3.5",
            "-2.9, -2.9",
            "1e2, 100.0",
            ".5, 0.5"
    })
    void shouldParseFloatsLikePython(String value, double expected) {
        assertThat(PythonValues.toFloat(value)).isEqualTo(expected);
    }

    @Test
    void shouldTreatNullAndNanAsNaN() {

        assertThat(PythonValues.toFloat(null)).isNaN();
        assertThat(PythonValues.toFloat("nan")).isNaN();
        assertThat(PythonValues.toFloatOrNaN("abc")).isNaN();

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "1,5"})
    void shouldRejectStringsPythonCannotConvert(String value) {
        assertThatThrownBy(() -> PythonValues.toFloat(value)).isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldTruncateTowardsZeroLikePythonInt() {

        assertThat(PythonValues.toInt(5.92)).isEqualTo(5);
        assertThat(PythonValues.toInt(-5.92)).isEqualTo(-5);
        assertThatThrownBy(() -> PythonValues.toInt(Double.NaN)).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> PythonValues.toInt(Double.POSITIVE_INFINITY)).isInstanceOf(ArithmeticException.class);

    }

    @Test
    void shouldTreatOnlyNullAndEmptyStringAsMissing() {

        assertThat(PythonValues.hasValue(null)).isFalse();
        assertThat(PythonValues.hasValue("")).isFalse();
        assertThat(PythonValues.hasValue("0")).isTrue();
        assertThat(PythonValues.hasValue(0L)).isTrue();

    }

    @ParameterizedTest
    @CsvSource({
            "2025-01-05, 2025-01-05",
            "2025-1-5, 2025-01-05"
    })
    void shouldParseDatesLikeStrptime(String value, LocalDate expected) {
        assertThat(PythonValues.parseIsoDate(value)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "2025-13-01", "2025-02-30", "01-01-2025", "2025-01-01T00:00"})
    void shouldRejectDatesStrptimeRejects(String value) {
        assertThatThrownBy(() -> PythonValues.parseIsoDate(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFormatDates() {

        var date = LocalDate.of(2025, 3, 7);

        assertThat(PythonValues.formatIsoDate(date)).isEqualTo("2025-03-07");
        assertThat(PythonValues.formatDifferentDate(date)).isEqualTo("07-03-2025");
        assertThatThrownBy(() -> PythonValues.plusDays(date, 3_000_000)).isInstanceOf(IllegalStateException.class);

    }

}
