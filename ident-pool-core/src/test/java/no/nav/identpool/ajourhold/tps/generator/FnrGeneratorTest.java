package no.nav.identpool.ajourhold.tps.generator;

import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.google.common.collect.Ordering;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

@ExtendWith(MockitoExtension.class)
class FnrGeneratorTest {

    private static final int END_1900 = 499;
    private static final int START_1900 = 0;
    private static final int GENERATE_SIZE = 100;

    private LocalDate LOCAL_DATE = LocalDate.now();

    //TODO Test exceptions

    @Test
    void fnrGenererDescendingTest() {
        // This test will stop working 1. Jan 2040 :(
        LocalDate localDate = LocalDate.now();
        Map<LocalDate, List<String>> pinMap = IdentGenerator.genererIdenterMap(localDate, localDate.plusDays(1), Identtype.FNR);
        assertEquals(1, pinMap.size());
        assertTrue(Ordering.natural().reverse().isOrdered(pinMap.get(localDate)));
    }

    @Test
    void fnrGenererKjonnKriterier() {
        List<String> menn = generateIdents(Identtype.FNR, Kjoenn.MANN);
        List<String> kvinner = generateIdents(Identtype.FNR, Kjoenn.KVINNE);

        assertEquals(menn.size(), GENERATE_SIZE);
        menn.forEach(fnr -> assertFnrValues(fnr, Kjoenn.MANN, LOCAL_DATE));
        assertEquals(kvinner.size(), GENERATE_SIZE);
        kvinner.forEach(fnr -> assertFnrValues(fnr, Kjoenn.KVINNE, LOCAL_DATE));
    }

    @Test
    void dnrGenererKjonnKriterier() {
        List<String> menn = generateIdents(Identtype.DNR, Kjoenn.MANN);
        List<String> kvinner = generateIdents(Identtype.DNR, Kjoenn.KVINNE);

        assertEquals(menn.size(), GENERATE_SIZE);
        menn.forEach(dnr -> assertDnrValues(dnr, Kjoenn.MANN, LOCAL_DATE));
        assertEquals(kvinner.size(), GENERATE_SIZE);
        kvinner.forEach(dnr -> assertDnrValues(dnr, Kjoenn.KVINNE, LOCAL_DATE));
    }

    private List<String> generateIdents(Identtype identtype, Kjoenn kjoenn) {
        return IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(identtype)
                        .antall(GENERATE_SIZE)
                        .foedtEtter(LOCAL_DATE)
                        .kjoenn(kjoenn)
                        .build());
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
