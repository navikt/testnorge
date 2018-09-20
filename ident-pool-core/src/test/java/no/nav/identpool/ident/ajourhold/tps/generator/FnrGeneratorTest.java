package no.nav.identpool.ident.ajourhold.tps.generator;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.time.LocalDate;

import java.util.Arrays;

import com.google.common.collect.Ordering;

import org.junit.Test;

public class FnrGeneratorTest {

    @Test
    public void fnrGenererDescendingTest() {
        LocalDate localDate = LocalDate.now();
        String[] fnrs = FnrGenerator.genererIdenterArray(localDate, localDate);
        assertTrue(Ordering.natural().reverse().isOrdered(Arrays.asList(fnrs)));

        fnrs = FnrGenerator.genererIdenterArray(localDate, localDate.plusDays(1));
        assertFalse(Ordering.natural().reverse().isOrdered(Arrays.asList(fnrs)));
    }
}
