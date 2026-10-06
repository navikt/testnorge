package no.nav.testnav.apps.statusfrontend.fagsystem.arena;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface ArenaClient {

    Mono<ArenaResourceStatus> getUser(
            FunctionalTestEnvironment environment,
            RunId runId,
            ArenaRequest expectedRequest
    );

    Mono<Void> createUser(
            FunctionalTestEnvironment environment,
            RunId runId,
            ArenaRequest request
    );

    Mono<Void> deactivateUser(FunctionalTestEnvironment environment, RunId runId);
}
