package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;
import java.util.List;

final class SkattekortTestData {

    static final String RESULT_OK = "skattekortopplysningerOK";
    static final String RESULT_NOT_TAX_CARD = "ikkeSkattekort";

    private SkattekortTestData() {
    }

    static int incomeYear(FunctionalTestContext context) {
        return context.startedAt().atZone(ZoneOffset.UTC).getYear();
    }

    static SkattekortRequest taxCard(String ident, FunctionalTestContext context) {
        var date = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        return new SkattekortRequest(
                ident,
                new SkattekortData(
                        date.toString(),
                        date.getYear(),
                        RESULT_OK,
                        List.of(new SkattekortData.Forskuddstrekk(
                                "loennFraNAV",
                                new SkattekortData.Frikort(50000))),
                        List.of()));
    }

    static SkattekortRequest notTaxCard(String ident, FunctionalTestContext context) {
        var date = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        return new SkattekortRequest(
                ident,
                new SkattekortData(
                        date.toString(),
                        date.getYear(),
                        RESULT_NOT_TAX_CARD,
                        List.of(),
                        List.of()));
    }
}
