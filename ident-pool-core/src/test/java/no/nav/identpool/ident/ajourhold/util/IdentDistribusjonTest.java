package no.nav.identpool.ident.ajourhold.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import org.junit.Test;

public class IdentDistribusjonTest {

    private IdentDistribusjon identDistribusjon = new IdentDistribusjon();

    @Test
    public void totaltAntallPersonerPerDagSkalSummereTilCa50000() {
        Integer sum = 0;
        Integer antallPersonerPerDag;
        for (Integer age = 0; age < 100; age = age + 10) {
            antallPersonerPerDag = identDistribusjon.antallPersonerForDag(LocalDate.of(LocalDate.now().getYear()-age, 1, 1));
            for (Integer ageIteration = age; ageIteration < age+10; ageIteration++) {
                sum = sum + (LocalDate.now().minusYears(age).isLeapYear() ? 366*antallPersonerPerDag : 365*antallPersonerPerDag);
            }
        }

        assertThat(sum, is(greaterThan(40000)));
        assertThat(sum, is(lessThan(60000)));
    }
}