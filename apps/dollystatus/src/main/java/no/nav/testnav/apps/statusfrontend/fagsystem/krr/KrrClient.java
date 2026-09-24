package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface KrrClient {

    Mono<KrrResourceStatus> getContactInformation(RunId runId, KrrRequest expectedRequest);

    Mono<Void> createContactInformation(RunId runId, KrrRequest request);

    Mono<Void> deleteContactInformation(RunId runId);
}
