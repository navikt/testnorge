package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;

@UtilityClass
class NomTestData {

    static NomRequest request(String ident, FunctionalTestContext context) {
        return new NomRequest(
                ident,
                "Testesen",
                "Test",
                null,
                context.startedAt().atZone(ZoneOffset.UTC).toLocalDate().minusDays(2),
                null);
    }
}
