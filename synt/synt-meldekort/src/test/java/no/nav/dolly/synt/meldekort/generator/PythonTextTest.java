package no.nav.dolly.synt.meldekort.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PythonTextTest {

    @Test
    void shouldFormatValuesLikePythonStr() {

        assertThat(PythonText.lower(true)).isEqualTo("true");
        assertThat(PythonText.lower(null)).isEqualTo("nan");
        assertThat(PythonText.lower("J")).isEqualTo("j");
        assertThat(PythonText.str(5L)).isEqualTo("5");
        assertThat(PythonText.str(5.0)).isEqualTo("5.0");
        assertThat(PythonText.upper("arbs")).isEqualTo("ARBS");

    }

    @Test
    void shouldNegateValuesLikePythonNot() {

        assertThat(PythonText.not(true)).isFalse();
        assertThat(PythonText.not(false)).isTrue();
        assertThat(PythonText.not("X")).isFalse();
        assertThat(PythonText.not("")).isTrue();
        assertThat(PythonText.not(null)).isFalse();
        assertThat(PythonText.not(0L)).isTrue();

    }

    @Test
    void shouldFormatArbeidstimerOverrideLikeLegacyController() {

        assertThat(PythonText.number(7.0)).isEqualTo("7");
        assertThat(PythonText.number(7.5)).isEqualTo("7.5");
        assertThat(PythonText.number(0.0)).isEqualTo("0");

    }

}
