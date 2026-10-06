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
public class GetAfpOffentligCommand implements Callable<Mono<PensjonResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final RunId runId;
    private final String ident;
    private final String environment;
    private final String expectedTpId;
    private final Duration timeout;

    @Override
    public Mono<PensjonResourceStatus> call() {
        return webClient.get()
                .uri("/pensjon/{miljo}/api/mock-oppsett/{ident}", environment, ident)
                .headers(headers -> PensjonCommandSupport.applyHeaders(headers, token, runId))
                .exchangeToMono(response -> PensjonCommandSupport.readLookup(response, this::toStatus))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private PensjonResourceStatus toStatus(JsonNode response) {
        if (!response.isObject()) {
            throw new IllegalStateException("AFP-oppslaget returnerte ugyldig respons.");
        }
        var directCalls = response.path("direktekall");
        var mocks = response.path("mocksvar");
        var empty = (!directCalls.isArray() || directCalls.isEmpty())
                && (!mocks.isArray() || mocks.isEmpty());
        var expectedDataPresent = false;
        if (mocks.isArray()) {
            for (var mock : mocks) {
                if (expectedTpId.equals(mock.path("tpId").asString())
                        && "INNVILGET".equals(mock.path("statusAfp").asString())) {
                    expectedDataPresent = true;
                }
            }
        }
        return new PensjonResourceStatus(empty, expectedDataPresent);
    }
}
