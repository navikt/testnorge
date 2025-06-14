package no.nav.testnav.identpool.service;

import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.Ordering;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Generering av identer")
class IdentGeneratorServiceTest {

    private static final int END_1900 = 499;
    private static final int START_1900 = 0;
    private static final int GENERATE_SIZE = 100;

    private final LocalDate timeNow = LocalDate.now();

    private final IdentGeneratorService identGeneratorService = new IdentGeneratorService();

    @Test
    @DisplayName("Skal feile når FOM er etter TOM")
    void shouldThrowToDateAfterFromDate() {

        var request = createRequest(Identtype.FNR, Kjoenn.MANN)
                .foedtFoer(timeNow.minusDays(2))
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> identGeneratorService.genererIdenter(request));
    }

    @Test
    @DisplayName("Skal ikke feile når ønsket antall ikke kan genereres")
    void shouldNotThrowTooFewIdents() {

        var requestedAmount = 500;
        var request = createRequest(Identtype.FNR, Kjoenn.MANN)
                .antall(requestedAmount)
                .build();

        var result = identGeneratorService.genererIdenter(request);

        assertThat(result.size(), is(lessThan(requestedAmount)));
    }

    @Test
    @DisplayName("Skal ikke generere identer i sortert rekkefølge")
    void fnrGenererDescendingTest() {

        // This test will stop working 1. Jan 2040 :(
        var localDate = LocalDate.now();
        Map<LocalDate, List<String>> pinMap =
                identGeneratorService.genererIdenterMap(localDate, localDate.plusDays(1), Identtype.FNR, false);
        assertThat(pinMap.size(), is(equalTo(1)));
        assertThat(Ordering.natural().reverse().isOrdered(pinMap.get(localDate)), is(false));
    }

    @Test
    @DisplayName("Skal generere angitt antall identer med FNR")
    void fnrGenererKjonnKriterier() {

        var menn = generateIdents(Identtype.FNR, Kjoenn.MANN);
        var kvinner = generateIdents(Identtype.FNR, Kjoenn.KVINNE);

        assertThat(menn.size(), is(equalTo(GENERATE_SIZE)));
        menn.forEach(fnr -> assertFnrValues(fnr, Kjoenn.MANN, timeNow));
        assertThat(kvinner.size(), is(equalTo(GENERATE_SIZE)));
        kvinner.forEach(fnr -> assertFnrValues(fnr, Kjoenn.KVINNE, timeNow));
    }

    @Test
    @DisplayName("Skal generere angitt antall identer med DNR")
    void dnrGenererKjonnKriterier() {

        var menn = generateIdents(Identtype.DNR, Kjoenn.MANN);
        var kvinner = generateIdents(Identtype.DNR, Kjoenn.KVINNE);

        assertThat(menn.size(), is(equalTo(GENERATE_SIZE)));
        menn.forEach(dnr -> assertDnrValues(dnr, Kjoenn.MANN, timeNow));
        assertThat(kvinner.size(), is(equalTo(GENERATE_SIZE)));
        kvinner.forEach(dnr -> assertDnrValues(dnr, Kjoenn.KVINNE, timeNow));
    }

    @Test
    @DisplayName("Skal generere angitt antall identer med BOST")
    void bostGenererKjonnKriterier() {

        var menn = generateIdents(Identtype.BOST, Kjoenn.MANN);
        var kvinner = generateIdents(Identtype.BOST, Kjoenn.KVINNE);

        assertThat(menn.size(), is(equalTo(GENERATE_SIZE)));
        menn.forEach(bnr -> assertBnrValues(bnr, Kjoenn.MANN, timeNow));
        assertThat(kvinner.size(), is(equalTo(GENERATE_SIZE)));
        kvinner.forEach(bnr -> assertBnrValues(bnr, Kjoenn.KVINNE, timeNow));
    }

    private Set<String> generateIdents(Identtype identtype, Kjoenn kjoenn) {

        return identGeneratorService.genererIdenter(
                createRequest(identtype, kjoenn).build());
    }

    private HentIdenterRequest.HentIdenterRequestBuilder createRequest(Identtype identtype, Kjoenn kjoenn) {

        return HentIdenterRequest.builder()
                .identtype(identtype)
                .antall(GENERATE_SIZE)
                .foedtEtter(timeNow)
                .foedtFoer(timeNow)
                .kjoenn(kjoenn);
    }

    private void assertFnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {

        assertThat(fnr.matches("\\d{11}"), is(true));
        assertThat(getKjoenn(fnr), is(equalTo(expectedKjoenn)));
        assertThat(getBirthdate(fnr, false, false), is(equalTo(expectedDate)));
        assertThat(getNumericValue(fnr.charAt(0)), is(lessThan(4)));
    }

    private void assertDnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {

        assertThat(fnr.matches("\\d{11}"), is(true));
        assertThat(getKjoenn(fnr), is(equalTo(expectedKjoenn)));
        assertThat(getBirthdate(fnr, true, false), is(equalTo(expectedDate)));
        assertThat(getNumericValue(fnr.charAt(0)), is(greaterThan(3)));
    }

    private void assertBnrValues(String fnr, Kjoenn expectedKjoenn, LocalDate expectedDate) {

        assertThat(fnr.matches("\\d{11}"), is(true));
        assertThat(getKjoenn(fnr), is(equalTo(expectedKjoenn)));
        assertThat(getBirthdate(fnr, false, true), is(equalTo(expectedDate)));
        assertThat(getNumericValue(fnr.charAt(2)), is(greaterThan(1)));
    }

    private Kjoenn getKjoenn(String fnr) {

        return getNumericValue(fnr.charAt(8)) % 2 == 0 ? Kjoenn.KVINNE : Kjoenn.MANN;
    }

    private LocalDate getBirthdate(String fnr, boolean dnr, boolean bnr) {

        int day = parseInt(fnr.substring(0, 2));
        int month = parseInt(fnr.substring(2, 4));
        String year = fnr.substring(4, 6);
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
