package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface TpsMessagingClient {

    Mono<TpsEgenansattResourceStatus> getEgenansatt(
            RunId runId,
            List<String> environments,
            LocalDate expectedFromDate
    );

    Mono<Void> createEgenansatt(
            RunId runId,
            List<String> environments,
            LocalDate fromDate
    );

    Mono<Void> deleteEgenansatt(RunId runId, List<String> environments);
}
