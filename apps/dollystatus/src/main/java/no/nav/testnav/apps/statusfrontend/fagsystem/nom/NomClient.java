package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface NomClient {

    Mono<NomResourceStatus> getResource(RunId runId, NomRequest expectedRequest);

    Mono<Void> createResource(RunId runId, NomRequest request);

    Mono<Void> closeResource(RunId runId, LocalDate endDate);
}
