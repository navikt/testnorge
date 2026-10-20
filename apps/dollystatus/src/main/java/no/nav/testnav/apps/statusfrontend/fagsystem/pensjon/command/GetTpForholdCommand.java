package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.PensjonResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpForholdCommand implements Callable<Mono<PensjonResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final RunId runId;
    private final String ident;
    private final String environment;
    private final String expectedOrdning;
    private final Duration timeout;

    @Override
    public Mono<PensjonResourceStatus> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pensjon/api/v1/tp/forhold")
                        .queryParam("fnr", ident)
                        .queryParam("miljo", environment)
                        .build())
                .headers(headers -> PensjonCommandSupport.applyHeaders(headers, token, runId))
                .exchangeToMono(response -> PensjonCommandSupport.readLookup(response, this::toStatus))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private PensjonResourceStatus toStatus(JsonNode response) {
        if (!response.isArray()) {
            throw new IllegalStateException("TP-oppslaget returnerte ugyldig respons.");
        }
        var expectedDataPresent = false;
        for (var relationship : response) {
            if (expectedOrdning.equals(relationship.path("ordning").asString())) {
                expectedDataPresent = true;
            }
        }
        return new PensjonResourceStatus(response.isEmpty(), expectedDataPresent);
    }
}
