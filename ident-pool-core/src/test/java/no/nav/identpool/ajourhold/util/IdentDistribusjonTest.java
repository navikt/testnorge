package no.nav.identpool.ajourhold.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class IdentDistribusjonTest {

    private IdentDistribusjon identDistribusjon = new IdentDistribusjon();

    //TODO Sliter litt med å se verdien av denne testen ettersom mesteparten av kalkuleringen skjer i testen
    @Test
    void totaltAntallPersonerPerDagSkalSummereTilCa50000() {
        int sum = 0;
        int antallPersonerPerDag;
        for (int age = 0; age < 100; age = age + 10) {
            antallPersonerPerDag = identDistribusjon.antallPersonerPerDagPerAar(LocalDate.now().getYear() - age);
            for (int ageIteration = age; ageIteration < age + 10; ageIteration++) {
                sum = sum + (LocalDate.now().minusYears(age).isLeapYear() ? 366 * antallPersonerPerDag : 365 * antallPersonerPerDag);
            }
        }

        assertThat(sum, is(greaterThan(40000)));
        assertThat(sum, is(lessThan(70000)));
    }
}