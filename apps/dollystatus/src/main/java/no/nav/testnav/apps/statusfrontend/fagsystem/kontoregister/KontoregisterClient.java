package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import reactor.core.publisher.Mono;

public interface KontoregisterClient {

    Mono<KontoregisterResourceStatus> getAccount(
            RunId runId,
            OppdaterKontoRequestDTO expectedAccount
    );

    Mono<Void> createAccount(RunId runId, OppdaterKontoRequestDTO account);

    Mono<Void> deleteAccount(RunId runId);
}
