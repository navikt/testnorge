package no.nav.identpool.ajourhold.tps.generator;

import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.common.collect.Ordering;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.rs.v1.HentIdenterRequest;

@RunWith(MockitoJUnitRunner.class)
public class FnrGeneratorTest {

    private static final int END_1900 = 499;
    private static final int START_1900 = 0;

    //TODO Test exceptions

    @Test
    public void fnrGenererDescendingTest() {
        // This test will stop working 1. Jan 2040 :(
        LocalDate localDate = LocalDate.now();
        Map<LocalDate, List<String>> pinMap = IdentGenerator.genererIdenterMap(localDate, localDate.plusDays(1), Identtype.FNR);
        assertEquals(1, pinMap.size());
        assertTrue(Ordering.natural().reverse().isOrdered(pinMap.get(localDate)));
    }

    @Test
    public void fnrGenererKjonnKriterier() {
        LocalDate localDate = LocalDate.now();
        int size = 100;
        List<String> menn = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .antall(size)
                        .identtype(Identtype.FNR)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.MANN)
                        .build());

        assertEquals(menn.size(), size);
        menn.forEach(fnr -> assertFnrValues(fnr, Kjoenn.MANN, localDate));


        List<String> kvinner = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .antall(size)
                        .identtype(Identtype.FNR)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.KVINNE)
                        .build());

        assertEquals(kvinner.size(), size);
        kvinner.forEach(fnr -> assertFnrValues(fnr, Kjoenn.KVINNE, localDate));
    }

    @Test
    public void dnrGenererKjonnKriterier() {
        LocalDate localDate = LocalDate.now();
        int size = 100;
        List<String> menn = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.MANN)
                        .build());

        assertEquals(menn.size(), size);
        menn.forEach(dnr -> assertDnrValues(dnr, Kjoenn.MANN, localDate));

        List<String> kvinner = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.KVINNE)
                        .build());

        assertEquals(kvinner.size(), size);
        kvinner.forEach(dnr -> assertDnrValues(dnr, Kjoenn.KVINNE, localDate));
    }

    private void assertFnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {
        assertTrue(fnr.matches("\\d{11}"));
        assertEquals(getKjoenn(fnr), expectedKjoenn);
        assertEquals(getBirthdate(fnr, false), expectedDate);
        assertTrue(getNumericValue(fnr.charAt(0)) < 4);
    }

    private void assertDnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {
        assertTrue(fnr.matches("\\d{11}"));
        assertEquals(getKjoenn(fnr), expectedKjoenn);
        assertEquals(getBirthdate(fnr, true), expectedDate);
        assertTrue(getNumericValue(fnr.charAt(0)) > 3);
    }

    private Kjoenn getKjoenn(String fnr) {
        return getNumericValue(fnr.charAt(8)) % 2 == 0 ? Kjoenn.KVINNE : Kjoenn.MANN;
    }

    private LocalDate getBirthdate(String fnr, boolean dnr) {
        int day = parseInt(fnr.substring(0,2));
        int month = parseInt(fnr.substring(2,4));
        String year = fnr.substring(4,6);
        int periode = parseInt(fnr.substring(6, 9));
        String century = (periode >= START_1900 && periode <= END_1900) ? "19" : "20";
        if (dnr) {
            day = day - 40;
        }

        return LocalDate.of(parseInt(century + year), month, day);
    }
}
