package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;

final class NomTestData {

    private NomTestData() {
    }

    static NomRequest request(String ident, FunctionalTestContext context) {
        return new NomRequest(
                ident,
                "Testesen",
                "Test",
                null,
                context.startedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                null);
    }
}
