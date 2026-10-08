package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.AfpOffentligRequest;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateAfpOffentligCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final RunId runId;
    private final String ident;
    private final String environment;
    private final AfpOffentligRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.put()
                .uri("/pensjon/{miljo}/api/mock-oppsett/{ident}", environment, ident)
                .headers(headers -> PensjonCommandSupport.applyHeaders(headers, token, runId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
