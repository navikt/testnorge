package no.nav.identpool.service;

import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import no.nav.identpool.service.IdentGeneratorService;
import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.google.common.collect.Ordering;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Generering av identer")
class IdentGeneratorServiceTest {

    private static final int END_1900 = 499;
    private static final int START_1900 = 0;
    private static final int GENERATE_SIZE = 100;

    private LocalDate LOCAL_DATE = LocalDate.now();

    private IdentGeneratorService identGeneratorService = new IdentGeneratorService();

    @Test
    @DisplayName("Skal feile når FOM og TOM dato er like")
    void shouldThrowEqualDates() {
        HentIdenterRequest.HentIdenterRequestBuilder request = createRequest(Identtype.FNR, Kjoenn.MANN);
        request.foedtFoer(LOCAL_DATE);

        assertThrows(IllegalArgumentException.class, () -> identGeneratorService.genererIdenter(request.build()));
    }

    @Test
    @DisplayName("Skal feile når FOM er etter TOM")
    void shouldThrowToDateAfterFromDate() {
        HentIdenterRequest.HentIdenterRequestBuilder request = createRequest(Identtype.FNR, Kjoenn.MANN);
        request.foedtFoer(LOCAL_DATE.minusDays(2));

        assertThrows(IllegalArgumentException.class, () -> identGeneratorService.genererIdenter(request.build()));
    }

    @Test
    @DisplayName("Skal feile når ønsket antall ikke kan genereres")
    void shouldThrowToFewIdents() {
        HentIdenterRequest.HentIdenterRequestBuilder request = createRequest(Identtype.FNR, Kjoenn.MANN);
        request.antall(500);

        assertThrows(IllegalArgumentException.class, () -> identGeneratorService.genererIdenter(request.build()));
    }

    @Test
    @DisplayName("Skal generere identer i sortert rekkefølge")
    void fnrGenererDescendingTest() {
        // This test will stop working 1. Jan 2040 :(
        LocalDate localDate = LocalDate.now();
        Map<LocalDate, List<String>> pinMap = identGeneratorService.genererIdenterMap(localDate, localDate.plusDays(1), Identtype.FNR);
        assertEquals(1, pinMap.size());
        assertTrue(Ordering.natural().reverse().isOrdered(pinMap.get(localDate)));
    }

    @Test
    @DisplayName("Skal generere angitt antall identer med FNR")
    void fnrGenererKjonnKriterier() {
        List<String> menn = generateIdents(Identtype.FNR, Kjoenn.MANN);
        List<String> kvinner = generateIdents(Identtype.FNR, Kjoenn.KVINNE);

        assertEquals(menn.size(), GENERATE_SIZE);
        menn.forEach(fnr -> assertFnrValues(fnr, Kjoenn.MANN, LOCAL_DATE));
        assertEquals(kvinner.size(), GENERATE_SIZE);
        kvinner.forEach(fnr -> assertFnrValues(fnr, Kjoenn.KVINNE, LOCAL_DATE));
    }

    @Test
    @DisplayName("Skal generere angitt antall identer med DNR")
    void dnrGenererKjonnKriterier() {
        List<String> menn = generateIdents(Identtype.DNR, Kjoenn.MANN);
        List<String> kvinner = generateIdents(Identtype.DNR, Kjoenn.KVINNE);

        assertEquals(menn.size(), GENERATE_SIZE);
        menn.forEach(dnr -> assertDnrValues(dnr, Kjoenn.MANN, LOCAL_DATE));
        assertEquals(kvinner.size(), GENERATE_SIZE);
        kvinner.forEach(dnr -> assertDnrValues(dnr, Kjoenn.KVINNE, LOCAL_DATE));
    }

    @Test
    @DisplayName("Skal generere angitt antall identer med BOST")
    void bostGenererKjonnKriterier() {
        List<String> menn = generateIdents(Identtype.BOST, Kjoenn.MANN);
        List<String> kvinner = generateIdents(Identtype.BOST, Kjoenn.KVINNE);

        assertEquals(menn.size(), GENERATE_SIZE);
        menn.forEach(bnr -> assertBnrValues(bnr, Kjoenn.MANN, LOCAL_DATE));
        assertEquals(kvinner.size(), GENERATE_SIZE);
        kvinner.forEach(bnr -> assertBnrValues(bnr, Kjoenn.KVINNE, LOCAL_DATE));
    }

    private List<String> generateIdents(Identtype identtype, Kjoenn kjoenn) {
        return identGeneratorService.genererIdenter(
                createRequest(identtype, kjoenn).build());
    }

    private HentIdenterRequest.HentIdenterRequestBuilder createRequest(Identtype identtype, Kjoenn kjoenn) {
        return HentIdenterRequest.builder()
                .identtype(identtype)
                .antall(GENERATE_SIZE)
                .foedtEtter(LOCAL_DATE)
                .kjoenn(kjoenn);
    }

    private void assertFnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {
        assertTrue(fnr.matches("\\d{11}"));
        assertEquals(getKjoenn(fnr), expectedKjoenn);
        assertEquals(getBirthdate(fnr, false, false), expectedDate);
        assertTrue(getNumericValue(fnr.charAt(0)) < 4);
    }

    private void assertDnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {
        assertTrue(fnr.matches("\\d{11}"));
        assertEquals(getKjoenn(fnr), expectedKjoenn);
        assertEquals(getBirthdate(fnr, true, false), expectedDate);
        assertTrue(getNumericValue(fnr.charAt(0)) > 3);
    }

    private void assertBnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {
        assertTrue(fnr.matches("\\d{11}"));
        assertEquals(getKjoenn(fnr), expectedKjoenn);
        assertEquals(getBirthdate(fnr, false, true), expectedDate);
        assertTrue(getNumericValue(fnr.charAt(2)) > 1);
    }

    private Kjoenn getKjoenn(String fnr) {
        return getNumericValue(fnr.charAt(8)) % 2 == 0 ? Kjoenn.KVINNE : Kjoenn.MANN;
    }

    private LocalDate getBirthdate(String fnr, boolean dnr, boolean bnr) {
        int day = parseInt(fnr.substring(0,2));
        int month = parseInt(fnr.substring(2,4));
        String year = fnr.substring(4,6);
        int periode = parseInt(fnr.substring(6, 9));
        String century = (periode >= START_1900 && periode <= END_1900) ? "19" : "20";
        if (dnr) {
            day = day - 40;
        }
        if (bnr) {
            month = month - 20;
        }

        return LocalDate.of(parseInt(century + year), month, day);
    }
}
