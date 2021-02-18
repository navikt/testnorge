package no.nav.identpool.ajourhold;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

class IdentDistribusjonUtilTest {

    @Test
    void totaltAntallPersonerPerDagSkalSummereTilCa110000() {
        int sum = 0;
        int antallPersonerPerDag;
        for (int age = 0; age < 100; age = age + 10) {
            antallPersonerPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(LocalDate.now().getYear() - age);
            for (int ageIteration = age; ageIteration < age + 10; ageIteration++) {
                sum = sum + (LocalDate.now().minusYears(age).isLeapYear() ? 366 * antallPersonerPerDag : 365 * antallPersonerPerDag);
            }
        }

        assertThat(sum, is(greaterThan(110000)));
        assertThat(sum, is(lessThan(120000)));
    }
}