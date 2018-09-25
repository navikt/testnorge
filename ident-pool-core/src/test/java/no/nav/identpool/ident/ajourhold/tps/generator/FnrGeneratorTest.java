package no.nav.identpool.ident.ajourhold.tps.generator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.time.LocalDate;

import java.util.Arrays;
import java.util.Collections;

import com.google.common.collect.Ordering;

import org.junit.Test;

public class FnrGeneratorTest {

    @Test
    public void fnrGenererDescendingTest() {
        LocalDate localDate = LocalDate.now();
        String[] fnrs = FnrGenerator.genererIdenter(localDate).toArray(new String[]{});
        assertTrue(Ordering.natural().reverse().isOrdered(Arrays.asList(fnrs)));

        String[] fnrs2 = FnrGenerator.genererIdenter(localDate, localDate.plusDays(1)).toArray(new String[]{});
        assertTrue(Ordering.natural().reverse().isOrdered(Arrays.asList(fnrs2)));

        String[][] fnrsArray = FnrGenerator.genererIdenterArray(localDate, localDate.plusDays(2));
        assertEquals(2, fnrsArray.length);
        assertTrue(Ordering.natural().reverse().isOrdered(Collections.singletonList(fnrs[0])));
        assertTrue(Ordering.natural().reverse().isOrdered(Collections.singletonList(fnrs[1])));
    }
}
