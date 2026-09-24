package no.nav.testnav.apps.statusfrontend.fagsystem.arena;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;
import java.util.List;

final class ArenaTestData {

    private ArenaTestData() {
    }

    static ArenaRequest request(String ident, FunctionalTestContext context) {
        return new ArenaRequest(List.of(new ArenaRequest.User(
                ident,
                environmentName(context),
                context.startedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                "IKVAL",
                true)));
    }

    static String environmentName(FunctionalTestContext context) {
        return context.environment().name().toLowerCase();
    }
}
