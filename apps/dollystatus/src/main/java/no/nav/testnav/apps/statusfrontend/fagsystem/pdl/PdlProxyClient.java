package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface PdlProxyClient {

    Mono<Boolean> personExists(FunctionalTestEnvironment environment, RunId runId);
}
