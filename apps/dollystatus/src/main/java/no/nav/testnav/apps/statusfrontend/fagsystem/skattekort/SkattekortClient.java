package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface SkattekortClient {

    Mono<SkattekortResourceStatus> getTaxCard(
            FunctionalTestEnvironment environment,
            RunId runId,
            int incomeYear
    );

    Mono<Void> createTaxCard(
            FunctionalTestEnvironment environment,
            RunId runId,
            SkattekortRequest request
    );
}
