package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;

final class KrrTestData {

    private KrrTestData() {
    }

    static KrrRequest request(String ident, FunctionalTestContext context) {
        var timestamp = context.startedAt().atZone(ZoneOffset.UTC);
        return new KrrRequest(
                ident,
                timestamp,
                false,
                true,
                "+4740000000",
                "dollystatus@example.invalid",
                "nb",
                timestamp,
                timestamp,
                timestamp,
                timestamp,
                timestamp,
                timestamp);
    }
}
