package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface PensjonClient {

    Mono<Void> createTpForhold(FunctionalTestEnvironment environment, RunId runId);

    Mono<PensjonResourceStatus> getTpForhold(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> deleteTpForhold(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> createPopp(FunctionalTestEnvironment environment, RunId runId);

    Mono<PensjonResourceStatus> getPopp(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> deletePopp(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> createAfpOffentlig(FunctionalTestEnvironment environment, RunId runId);

    Mono<PensjonResourceStatus> getAfpOffentlig(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> deleteAfpOffentlig(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> createPensjonsavtale(RunId runId);

    Mono<PensjonResourceStatus> getPensjonsavtale(FunctionalTestEnvironment environment, RunId runId);

    Mono<Void> deletePensjonsavtale(RunId runId);
}
