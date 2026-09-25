package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

final class SkjermingsregisterTestData {

    static final String FIRST_NAME = "Dollystatus";
    static final String LAST_NAME = "Testperson";

    private SkjermingsregisterTestData() {
    }

    static SkjermingsregisterRequest activeRequest(
            String ident,
            FunctionalTestContext context
    ) {
        var from = context.startedAt()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.SECONDS);
        return new SkjermingsregisterRequest(
                LAST_NAME,
                FIRST_NAME,
                ident,
                from,
                from.plusDays(1));
    }

    static SkjermingsregisterRequest terminatedRequest(
            String ident,
            FunctionalTestContext context
    ) {
        var activeRequest = activeRequest(ident, context);
        return new SkjermingsregisterRequest(
                activeRequest.etternavn(),
                activeRequest.fornavn(),
                activeRequest.personident(),
                activeRequest.skjermetFra(),
                activeRequest.skjermetFra());
    }
}
