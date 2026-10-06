package no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface ArbeidssoekerregisteretClient {

    Mono<ArbeidssoekerregisteretResourceStatus> getRegistration(
            RunId runId,
            ArbeidssoekerregisteretRequest expectedRequest
    );

    Mono<Void> createRegistration(RunId runId, ArbeidssoekerregisteretRequest request);

    Mono<Void> deleteRegistration(RunId runId);
}
