package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command;

import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonOperationResponse;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.Set;
import java.util.function.Function;

final class PensjonCommandSupport {

    private PensjonCommandSupport() {
    }

    static void applyHeaders(HttpHeaders headers, String token, RunId runId) {
        headers.setBearerAuth(token);
        headers.set("Nav-Call-Id", "Dollystatus-" + runId.value());
        headers.set("Nav-Consumer-Id", "Dolly");
    }

    static Mono<Void> requireSuccessful(
            Mono<PensjonOperationResponse> response,
            Set<String> expectedEnvironments
    ) {
        return response
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "Pensjon-operasjonen returnerte ingen miljøstatus.")))
                .doOnNext(value -> value.requireSuccessful(expectedEnvironments))
                .then();
    }

    static Mono<PensjonResourceStatus> readLookup(
            ClientResponse response,
            Function<JsonNode, PensjonResourceStatus> mapper
    ) {
        if (response.statusCode() == HttpStatus.NOT_FOUND
                || response.statusCode() == HttpStatus.NO_CONTENT) {
            return response.releaseBody().thenReturn(PensjonResourceStatus.emptyStatus());
        }
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(JsonNode.class)
                    .map(mapper)
                    .defaultIfEmpty(PensjonResourceStatus.emptyStatus());
        }
        return response.createException()
                .flatMap(Mono::error);
    }
}
