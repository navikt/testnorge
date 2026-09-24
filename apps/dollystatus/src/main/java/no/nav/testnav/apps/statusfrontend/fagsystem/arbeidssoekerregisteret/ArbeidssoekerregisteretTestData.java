package no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.LocalDate;

final class ArbeidssoekerregisteretTestData {

    private ArbeidssoekerregisteretTestData() {
    }

    static ArbeidssoekerregisteretRequest request(String ident, FunctionalTestContext context) {
        var appliesFrom = LocalDate.of(2025, 1, 1);
        var appliesTo = LocalDate.of(2025, 1, 31);
        return new ArbeidssoekerregisteretRequest(
                ident,
                "SLUTTBRUKER",
                "Dolly",
                "Dollystatus-" + context.runId().value(),
                "4",
                true,
                true,
                "HAR_SAGT_OPP",
                new ArbeidssoekerregisteretRequest.Jobbsituasjonsdetaljer(
                        appliesFrom,
                        appliesTo,
                        2522,
                        "Programvareutvikler",
                        100,
                        appliesTo,
                        appliesTo.minusDays(1)),
                false,
                false);
    }
}
